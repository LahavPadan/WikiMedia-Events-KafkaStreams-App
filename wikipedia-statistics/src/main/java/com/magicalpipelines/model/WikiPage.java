package com.magicalpipelines.model;

public class WikiPage implements TreeValue {
  private String title;
  private int numModifications;

  public WikiPage(WikiEvent wikiEvent) {
    this.title = wikiEvent.getPageTitle();
    this.numModifications = 1;
  }

  @Override
  public int getScore() {
    return numModifications;
  }

  public int getNumModifications() {
    return numModifications;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public boolean setScore(int score) {
    numModifications = score;
    return true;
  }

  @Override
  public String toString() {
    return "{"
        + " Page ='"
        + getTitle()
        + "'"
        + ", Modifications ='"
        + getNumModifications()
        + "'"
        + "}";
  }
}
