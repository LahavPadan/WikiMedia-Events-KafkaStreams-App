package com.magicalpipelines;

import com.magicalpipelines.model.WikiPage;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RestService {
  private final HostInfo hostInfo;
  private final KafkaStreams streams;

  private final List<String> timeRanges = Arrays.asList("month", "week", "day", "hour");

  private final List<String> services =
      Arrays.asList(
          "countPagesCreated", "countPagesModified", "mostActiveUsers", "mostActivePages");
  private final List<String> filters = Arrays.asList("language", "userType");
  private static final Logger log = LoggerFactory.getLogger(RestService.class);

  RestService(HostInfo hostInfo, KafkaStreams streams) {
    this.hostInfo = hostInfo;
    this.streams = streams;
  }

  void start() {
    Javalin app = Javalin.create().start(hostInfo.port());

    String baseUrl = "/api.wikiStats/v1/";

    // Query also the 'all' store in method
    /** Pie charts: all entries */
    app.get(baseUrl + "{time}/{filter}/{countType}", this::getAllCountPercentages);

    /** Most active queries */
    app.get(baseUrl + "{time}/{filter}/{filterParam}/mostActiveUsers", this::getKeyMostActiveUsers);
    app.get(baseUrl + "{time}/{filter}/{filterParam}/mostActivePages", this::getKeyMostActivePages);
  }

  <V> void getKey(
      KafkaStreams streams, HostInfo hostInfo, Context ctx, String storeName, String key) {

    KeyQueryMetadata metadata =
        streams.queryMetadataForKey(storeName, key, Serdes.String().serializer());
    // the local instance has this key
    if (hostInfo.equals(metadata.activeHost())) {
      log.info("Querying local store for key");

      ReadOnlyKeyValueStore<String, V> store =
          streams.store(
              StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));
      V stats = store.get(key);

      if (stats == null) {
        // stats wasn't found
        ctx.status(404);
        return;
      }

      ctx.json(stats);
      return;
    }

    // a remote instance has the key
    String remoteHost = metadata.activeHost().host();
    int remotePort = metadata.activeHost().port();
    String url =
        String.format(
            "http://%s:%d/%s",
            // params
            remoteHost, remotePort, ctx.req.getRequestURL());

    // issue the request
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(url).build();

    try (Response response = client.newCall(request).execute()) {
      log.info("Querying remote store for key");
      ctx.result(response.body().string());
    } catch (Exception e) {
      ctx.status(500);
    }
  }

  void getAllCountPercentages(Context ctx) {
    String time = ctx.pathParam("time");
    String filter = ctx.pathParam("filter");
    String countType = ctx.pathParam("countType");
    // validate input

    /** get overall count */
    String storeName = String.join("-", Arrays.asList(time, "all", countType));
    this.<Long>getKey(streams, hostInfo, ctx, storeName, ""); // key is: ""
    long totalCount = Long.parseLong(Objects.requireNonNull(ctx.resultString()));

    /** get every count */
    Map<String, Double> countPrecentages = new HashMap<>();

    storeName = String.join("-", Arrays.asList(time, filter, countType));

    ReadOnlyKeyValueStore<String, Long> store =
        streams.store(
            StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));

    try (KeyValueIterator<String, Long> range = store.all()) {
      while (range.hasNext()) {
        KeyValue<String, Long> next = range.next();
        double countPercentages = (next.value * 100.0) / totalCount;
        countPrecentages.put(next.key, countPercentages);
      }
      ctx.json(countPrecentages);

    } catch (Exception e) {
      // log error
      log.error("Could not get {} count: {}", countType, e);
    }
  }

  void getKeyMostActiveUsers(Context ctx) {
    String time = ctx.pathParam("time");
    String filter = ctx.pathParam("filter");
    String filterParam = ctx.pathParam("filterParam");
    // validate input

    String storeName = String.join("-", Arrays.asList(time, filter, "MostActiveUsers"));
    this.<SortedWikiUsers>getKey(streams, hostInfo, ctx, storeName, filterParam);
  }

  void getKeyMostActivePages(Context ctx) {
    String time = ctx.pathParam("time");
    String filter = ctx.pathParam("filter");
    String filterParam = ctx.pathParam("filterParam");
    // validate input

    String storeName = String.join("-", Arrays.asList(time, filter, "MostActivePages"));
    this.<SortedWikiStatistic<WikiPage>>getKey(streams, hostInfo, ctx, storeName, filterParam);
  }
}
