package com.magicalpipelines.serialization.json;

import com.google.gson.reflect.TypeToken;
import com.magicalpipelines.SortedWikiStatistic;
import com.magicalpipelines.SortedWikiUsers;
import com.magicalpipelines.model.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class JsonSerdes {

  public static Serde<WikiEvent> WikiEvent() {
    JsonSerializer<WikiEvent> serializer = new JsonSerializer<>();
    JsonDeserializer<WikiEvent> deserializer = new JsonDeserializer<>(WikiEvent.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<WikiUser> WikiUser() {
    JsonSerializer<WikiUser> serializer = new JsonSerializer<>();
    JsonDeserializer<WikiUser> deserializer = new JsonDeserializer<>(WikiUser.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<WikiPage> WikiPage() {
    JsonSerializer<WikiPage> serializer = new JsonSerializer<>();
    JsonDeserializer<WikiPage> deserializer = new JsonDeserializer<>(WikiPage.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<SortedWikiUsers> SortedWikiUsers() {
    JsonSerializer<SortedWikiUsers> serializer = new JsonSerializer<>();
    JsonDeserializer<SortedWikiUsers> deserializer = new JsonDeserializer<>(SortedWikiUsers.class);
    return Serdes.serdeFrom(serializer, deserializer);
  }

  public static Serde<SortedWikiStatistic<WikiPage>> SortedWikiPage() {
    JsonSerializer<SortedWikiStatistic<WikiPage>> serializer = new JsonSerializer<>();
    JsonDeserializer<SortedWikiStatistic<WikiPage>> deserializer =
        new JsonDeserializer<>(new TypeToken<SortedWikiStatistic<WikiPage>>() {}.getType());
    return Serdes.serdeFrom(serializer, deserializer);
  }
}

// public static Serde<TreeMap<String,WikiUser>> TreeMapStringWikiUser(){
  //   JsonSerializer<SortedWikiStatistic<WikiPage>> serializer = new JsonSerializer<>();
  //   Type listOfTestObject = new TypeToken<TreeMap<String,WikiUser>>(){}.getType();
  //   String s = gson.toJson(list, listOfTestObject);
  //   List<TestObject> list2 = gson.fromJson(s, listOfTestObject);
  // }
