package com.magicalpipelines.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class WikiUser implements TreeValue, Serializable {
  @SerializedName("UserName")
  private String userName;

  @SerializedName("IsBot")
  private boolean isBot;

  @SerializedName("NumCommits")
  private int numCommits;

  public WikiUser(WikiEvent wikiEvent) {
    this.userName = wikiEvent.getUserName();
    this.isBot = wikiEvent.getIsBot();
    this.numCommits = 1;
  }

  public WikiUser(WikiUser other) {
    this.userName = other.userName;
    this.isBot = other.isBot;
    this.numCommits = other.numCommits;
  }

  public WikiUser(String userName, boolean isBot) {
    this.userName = userName;
    this.isBot = isBot;
    this.numCommits = 1;
  }

  public String getUserName() {
    return userName;
  }

  public boolean getIsBot() {
    return isBot;
  }

  public int getNumCommits() {
    return numCommits;
  }

  @Override
  public int getScore() {
    return numCommits;
  }

  @Override
  public boolean setScore(final int score) {
    numCommits = score;
    return true;
  }

  @Override
  public String toString() {
    return "{"
        + " UserName='"
        + getUserName()
        + "'"
        + ", isBot='"
        + getIsBot()
        + "'"
        + ", numCommits='"
        + getNumCommits()
        + "'"
        + "}";
  }
}
