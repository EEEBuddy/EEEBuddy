package com.example.EEEBuddy;

public class NotificationModel {

    private String from, type, groupChatID, status;

    public NotificationModel() {
    }

    public NotificationModel(String from, String type, String status) {
        this.from = from;
        this.type = type;
        this.status = status;
    }

    public NotificationModel(String from, String type, String groupChatID, String status) {
        this.from = from;
        this.type = type;
        this.groupChatID = groupChatID;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
