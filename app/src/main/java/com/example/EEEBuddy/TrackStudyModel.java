package com.example.EEEBuddy;

public class TrackStudyModel {
    private String subject;
    private String task;
    private String duration;
    private String completion;
    private String satisfaction;
    private String groupSize;
    private String method;
    private String location;
    private String remarks;

    public TrackStudyModel(){

    }

    public TrackStudyModel(String subject, String task, String duration, String completion, String satisfaction, String groupSize, String method, String location, String remarks) {
        this.subject = subject;
        this.task = task;
        this.duration = duration;
        this.completion = completion;
        this.satisfaction = satisfaction;
        this.groupSize = groupSize;
        this.method = method;
        this.location = location;
        this.remarks = remarks;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCompletion() {
        return completion;
    }

    public void setCompletion(String completion) {
        this.completion = completion;
    }

    public String getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(String satisfaction) {
        this.satisfaction = satisfaction;
    }

    public String getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(String groupSize) {
        this.groupSize = groupSize;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
