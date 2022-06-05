package com.magicalpipelines;

import com.google.gson.annotations.SerializedName;
import com.magicalpipelines.model.WikiUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortedWikiUsers implements Serializable {

  @SerializedName("StrKeyTree")
  private Map<String, WikiUser> strKeyTree = new HashMap<>();

  public SortedWikiUsers add(String name, WikiUser _newRecord) {
    System.out.println("JOJO " + name);

    WikiUser newRecord = new WikiUser(_newRecord);
    WikiUser record = strKeyTree.get(name);
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
