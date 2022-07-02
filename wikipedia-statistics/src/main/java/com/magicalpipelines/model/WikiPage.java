package com.magicalpipelines.model;

import java.io.Serializable;

public class WikiPage implements TreeValue, Serializable {
  private String title;
  private int numModifications;

  public WikiPage(WikiEvent wikiEvent) {
    this.title = wikiEvent.getPageTitle();
    this.numModifications = 1;
  }

  public WikiPage(WikiPage other) {
    this.title = other.getTitle();
    this.numModifications = other.getNumModifications();
  }

  public WikiPage(final String title) {
    this.title = title;
    this.numModifications = 1;
  }

  public int getNumModifications() {
    return numModifications;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public int getScore() {
    return numModifications;
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
