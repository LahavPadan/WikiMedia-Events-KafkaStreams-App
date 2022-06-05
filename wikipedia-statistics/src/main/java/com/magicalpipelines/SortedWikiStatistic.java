package com.magicalpipelines;

import com.magicalpipelines.model.TreeValue;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SortedWikiStatistic<V extends TreeValue> {
  private TreeMap<String, V> stringKeyTree = new TreeMap<>();
  private TreeMap<Integer, TreeMap<String, V>> scoreKeyTree = new TreeMap<>();
  private TreeMap<String, V> Jello = new TreeMap<>();

  public SortedWikiStatistic<V> add(final String name, final V newRecord) {
    V recordooooooooo = Jello.get(name);

    V record = stringKeyTree.get(name);
    int newScore = newRecord.getScore();

    /** previous version of record already exists */
    if (record != null) {
      System.out.println(record.toString());
      System.out.println(newRecord.toString());
      int score = record.getScore();

      /** update scoreKeyTree - remove previous */
      System.out.println("score " + score);
      var scoreRecords = scoreKeyTree.get(score);
      scoreRecords.remove(name);
      if (scoreRecords.isEmpty()) {
        scoreKeyTree.remove(score);
      }

      newScore = score + newScore;
      newRecord.setScore(newScore);
    }
    // update trees

    /** update stringKeyTree - insert */
    stringKeyTree.put(name, newRecord);
    Jello.put(name, newRecord);

    /** update scoreKeyTree - insert */
    TreeMap<String, V> newScoreRecords = scoreKeyTree.get(newScore);
    if (newScoreRecords == null) {
      newScoreRecords = new TreeMap<>();
      scoreKeyTree.put(newScore, newScoreRecords);
    }
    newScoreRecords.put(name, newRecord);

    // TreeMap<String, V> newScoreRecords =
    //     scoreKeyTree.computeIfAbsent(
    //         newScore, k -> new TreeMap<>()); // basically scoreKeyTree.get(newScore)
    // newScoreRecords.put(name, newRecord);
    return this;
  }

  public List<V> toList() {
    List<V> elements = new ArrayList<>();

    var entry = scoreKeyTree.firstEntry();

    for (int i = 0; entry != null; entry = scoreKeyTree.lowerEntry(entry.getKey())) {
      var scoreRecords = entry.getValue();
      var e = scoreRecords.firstEntry();
      for (; e != null && i < 5; e = scoreRecords.lowerEntry(e.getKey()), i++) {
        elements.add(e.getValue());
      }
    }
    return elements;
  }

  public String toString() {
    return this.toList().stream().map(Object::toString).collect(Collectors.joining(", "));
  }
}
