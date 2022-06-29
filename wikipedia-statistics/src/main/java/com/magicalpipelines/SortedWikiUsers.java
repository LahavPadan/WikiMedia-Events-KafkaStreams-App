package com.magicalpipelines;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.magicalpipelines.model.WikiUser;
import com.magicalpipelines.serialization.json.JsonDeserializer2;
import com.magicalpipelines.serialization.json.JsonSerializer2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortedWikiUsers implements Serializable {

  @JsonProperty("map")
  @JsonSerialize(using = JsonSerializer2.class)
  @JsonDeserialize(using = JsonDeserializer2.class, as = WikiUser.class)
  private Map<String, WikiUser> strKeyTree = new HashMap<>();

  public SortedWikiUsers add(String name, WikiUser _newRecord) {
    WikiUser newRecord = new WikiUser(_newRecord);
    WikiUser record = strKeyTree.get(name);
    System.out.println("JOJO " + record.toString());
    int newScore = newRecord.getScore();

    /** previous version of record already exists */
    if (record != null) {
      int score = record.getScore();

      newScore = score + newScore;
      newRecord.setScore(newScore);
    }
    /** update strKeyTree - insert */
    strKeyTree.put(name, newRecord);

    System.out.println("YARD");
    System.out.println(
        this.toList().stream().map(Object::toString).collect(Collectors.joining(", ")));

    return this;
  }

  public List<WikiUser> toList() {
    List<WikiUser> elements = new ArrayList<>(strKeyTree.values());

    // Note: the argument to compare are reversed. So, the list will be decreasing
    elements.sort((record1, record2) -> Integer.compare(record2.getScore(), record1.getScore()));
    return elements;
  }

  @Override
  public String toString() {
    return "{" + " StrKeyTree='" + this.strKeyTree + "'" + "}";
  }
}
