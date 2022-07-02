package com.magicalpipelines;
import com.magicalpipelines.model.WikiUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.magicalpipelines.model.TreeValue;

public class SortedWikiStatistic<V extends TreeValue> implements Serializable {

  private Map<String, V> sortedStats = new HashMap<>();

  public SortedWikiStatistic<V> add(String name, final V newRecord) {
    if (sortedStats.containsKey(name)) {
      newRecord.setScore(newRecord.getScore() + sortedStats.get(name).getScore());
    }
    /** update sortedStats */
    sortedStats.put(name, newRecord);
    return this;
  }

  public List<V> toList() {
    List<V> elements = new ArrayList<>(sortedStats.values());

    // Note: the argument to compare are reversed. So, the list will be decreasing
    elements.sort((record1, record2) -> Integer.compare(record2.getScore(), record1.getScore()));
    List<V> top5 = elements.stream().limit(5).collect(Collectors.toList());
    return top5;
  }
}
