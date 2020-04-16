package com.example.EEEBuddy;

public class NotificationModel {

    private String from, type, groupChatID;

    public NotificationModel() {
    }

    public NotificationModel(String from, String type) {
        this.from = from;
        this.type = type;
    }

    public NotificationModel(String from, String type, String groupChatID) {
        this.from = from;
        this.type = type;
        this.groupChatID = groupChatID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupChatID() {
        return groupChatID;
    }

    public void setGroupChatID(String groupChatID) {
        this.groupChatID = groupChatID;
    }
}
