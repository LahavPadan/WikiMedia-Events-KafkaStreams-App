package com.magicalpipelines.model;

import com.google.gson.annotations.SerializedName;

public class WikiEvent {
  @SerializedName("PageTitle")
  private String pageTitle;

  @SerializedName("IsBot")
  private Boolean isBot;

  @SerializedName("UserName")
  private String userName;

  @SerializedName("Date")
  private String date;

  @SerializedName("Lang")
  private String lang;

  @SerializedName("Type")
  private String type;

  @SerializedName("IsRevert")
  private Boolean isRevert;

  @SerializedName("RevertDetails")
  private Boolean revertDetails;

  public String getPageTitle() {
    return pageTitle;
  }

  public Boolean getIsBot() {
    return isBot;
  }

  public String getUserName() {
    if (userName == null) return "";
    return userName;
  }

  public String getDate() {
    return date;
  }

  public String getLang() {
    return lang;
  }

  public String getType() {
    if (type == null) return "";
    return type;
  }

  public Boolean getIsRevert() {
    return isRevert;
  }

  public Boolean getRevertDetails() {
    return revertDetails;
  }

  @Override
  public String toString() {
    return "{"
        + " Page ='"
        + getPageTitle()
        + "'"
        + ", Language ='"
        + getLang()
        + "'"
        + ", Action Type ='"
        + getType()
        + "'"
        + ", Action Date ='"
        + getDate()
        + "'"
        + ", User ='"
        + getUserName()
        + "'"
        + ", is bot? ='"
        + getIsBot()
        + "'"
        + ", is revert ='"
        + getIsRevert()
        + "'"
        + ", Revert details ='"
        + getRevertDetails()
        + "'"
        + "}";
  }
}
