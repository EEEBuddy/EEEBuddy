package com.example.EEEBuddy;

//define models.
public class StudyEvent {

    private String subjectCode;
    private String subjectName;
    private String task;
    private String location;
    private String date;
    private String startTime, endTime;
    private String groupSize;
    private String createdBy;
    private String eventID;


    public StudyEvent(){

    }


    public StudyEvent(String createdBy, String eventID){
        this.createdBy = createdBy;
        this.eventID = eventID;
    }


    public StudyEvent(String subjectCode, String subjectName, String task, String location, String date, String startTime, String endTime, String groupSize, String createdBy) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.task = task;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.groupSize = groupSize;
        this.createdBy = createdBy;
    }



    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getTask() {
        return task;
    }


    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGroupSize() {
        return groupSize;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGroupSize(String groupSize) {
        this.groupSize = groupSize;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }


}
