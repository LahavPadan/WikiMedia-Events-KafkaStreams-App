package com.magicalpipelines;

import com.magicalpipelines.model.*;
import com.magicalpipelines.serialization.json.JsonSerdes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;

public class WikipediaStatisticsTopology {
    
  public static Topology build() {
    // the builder is used to construct the topology
    StreamsBuilder builder = new StreamsBuilder();

    KStream<String, WikiEvent> wikiEvents =
        // register the last changes stream
        builder
            .stream("WikiEvents", Consumed.with(Serdes.String(), JsonSerdes.WikiEvent()))
            .selectKey((key, wikiEvent) -> "");

    // all records stream
    var allTimeRangedStreams = viewStreamInTimeRange("all", wikiEvents);
    statefulOperations(allTimeRangedStreams);

    // key by language
    KStream<String, WikiEvent> langStreams =
        wikiEvents.selectKey((key, wikiEvent) -> wikiEvent.getLang());

    var langTimeRangedStreams = viewStreamInTimeRange("per-language", langStreams);
    statefulOperations(langTimeRangedStreams);

    // key by isBot
    KStream<String, WikiEvent> userTypeStreams =
        wikiEvents.selectKey((key, wikiEvent) -> wikiEvent.getIsBot().toString());

    var userTypeTimeRangedStreams = viewStreamInTimeRange("per-userType", userTypeStreams);
    statefulOperations(userTypeTimeRangedStreams);

    return builder.build();
  }

  public static void statefulOperations(
    List<Map.Entry<String, KStream<String, WikiEvent>>> timeRangedStreams) {
  timeRangedStreams.forEach(
      (nameStreamPair) -> countPagesCreated(nameStreamPair.getKey(), nameStreamPair.getValue()));

  // Count how many pages were modified
  timeRangedStreams.forEach(
      (nameStreamPair) -> countPagesModified(nameStreamPair.getKey(), nameStreamPair.getValue()));

  // Sort *users* by most active
  timeRangedStreams.forEach(
      (nameStreamPair) -> mostActiveUsers(nameStreamPair.getKey(), nameStreamPair.getValue()));

  // Sort *pages* by most active
  timeRangedStreams.forEach(
      (nameStreamPair) -> mostActivePages(nameStreamPair.getKey(), nameStreamPair.getValue()));
}
    

  public static List<Map.Entry<String, KStream<String, WikiEvent>>> viewStreamInTimeRange(
      String streamName, KStream<String, WikiEvent> stream) {
    // branch by hour, day, week and month
    Instant todayDate = Instant.now();
    List<Map.Entry<String, KStream<String, WikiEvent>>> timeRangeStreams = new ArrayList<>();

    timeRangeStreams.add(
        Map.entry(
            "month-" + streamName,
            stream.filter(
                (key, wikiEvent) -> {
                  return ChronoUnit.DAYS.between(todayDate, Instant.parse(wikiEvent.getDate()))
                      <= 30;
                })));

    timeRangeStreams.add(
        Map.entry(
            "week-" + streamName,
            stream.filter(
                (key, wikiEvent) -> {
                  return ChronoUnit.DAYS.between(todayDate, Instant.parse(wikiEvent.getDate()))
                      <= 7;
                })));

    timeRangeStreams.add(
        Map.entry(
            "day-" + streamName,
            stream.filter(
                (key, wikiEvent) -> {
                  return ChronoUnit.DAYS.between(todayDate, Instant.parse(wikiEvent.getDate()))
                      <= 1;
                })));

    timeRangeStreams.add(
        Map.entry(
            "hour-" + streamName,
            stream.filter(
                (key, wikiEvent) -> {
                    return ChronoUnit.HOURS.between(todayDate, Instant.parse(wikiEvent.getDate()))
                        <= 1;
                })));

    return timeRangeStreams;
  }

  public static void countPagesCreated(String streamName, KStream<String, WikiEvent> Stream) {

    KStream<String, WikiEvent> createdPagesStream =
        Stream.filter((key, wikiEvent) -> wikiEvent.getType().equals("new"));

    KTable<String, Long> createdPagesCount =
        createdPagesStream
            .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.WikiEvent()))
            .count(Materialized.as(streamName + "-countPagesCreated"));
  }

  public static void countPagesModified(String streamName, KStream<String, WikiEvent> Stream) {

    KStream<String, WikiEvent> modifiedPagesStream =
        Stream.filter((key, wikiEvent) -> wikiEvent.getType().equals("edit"));

    KTable<String, Long> modifiedPagesCount =
        modifiedPagesStream
            .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.WikiEvent()))
            .count(Materialized.as(streamName + "-countPagesModified"));
  }

  public static void mostActiveUsers(String streamName, KStream<String, WikiEvent> stream) {
    /* Transform to WikiUser records */
    KGroupedStream<String, WikiUser> usersStream = 
        stream
            .mapValues((wikiEvent) -> new WikiUser(wikiEvent))
            .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.WikiUser()));

    /** The initial value of our aggregation will be a new SortedWikiStatistic instances */
    Initializer<SortedWikiStatistic<WikiUser>> activeUsersInitializer = SortedWikiUsers::new;

    /** The logic for aggregating user event is implemented in the SortedWikiStatistic.add method */
    Aggregator<String, WikiUser, SortedWikiStatistic<WikiUser>> activeUsersAdder =
        (key, user, aggregate) -> aggregate.add(user.getUserName(), user);

    /** Perform the aggregation, and materialize the underlying state store for querying */
    KTable<String, SortedWikiStatistic<WikiUser>> mostActiveUsers =
        usersStream.aggregate(
            activeUsersInitializer,
            activeUsersAdder,
            Materialized.<String, SortedWikiStatistic<WikiUser>, KeyValueStore<Bytes, byte[]>>as(
                    streamName + "-mostActiveUsers")
                .withKeySerde(Serdes.String())
                .withValueSerde(JsonSerdes.SortedWikiUsers()));
  }

  public static void mostActivePages(String streamName, KStream<String, WikiEvent> stream) {
    // Transform to user class
    KGroupedStream<String, WikiPage> usersStream =
        stream
            .mapValues((key, wikiEvent) -> new WikiPage(wikiEvent))
            .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.WikiPage()));

    /** The initial value of our aggregation will be a new SortedWikiStatistic instances */
    Initializer<SortedWikiStatistic<WikiPage>> activePagesInitializer = SortedWikiStatistic::new;

    /** The logic for aggregating user event is implemented in the SortedWikiStatistic.add method */
    Aggregator<String, WikiPage, SortedWikiStatistic<WikiPage>> activePagesAdder =
        (key, page, aggregate) -> aggregate.add(page.getTitle(), page);

    /** Perform the aggregation, and materialize the underlying state store for querying */
    KTable<String, SortedWikiStatistic<WikiPage>> mostActivePages =
        usersStream.aggregate(
            activePagesInitializer,
            activePagesAdder,
            Materialized.<String, SortedWikiStatistic<WikiPage>, KeyValueStore<Bytes, byte[]>>
                // give the state store an explicit name to make it available for interactive
                // queries
                as(streamName + "-mostActivePages")
                .withKeySerde(Serdes.String())
                .withValueSerde(JsonSerdes.SortedWikiPages()));
  }

}
