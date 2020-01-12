package com.example.EEEBuddy;

//define models.
public class StudyEvent {

    private String subjectCode;
    private String subjectName;
    private String task;
    private String location;
    private String date;
    private String time;
    private String groupSize;


    public StudyEvent(){

    }


    public StudyEvent(String subjectCode, String subjectName, String task, String location, String date, String time, String groupSize) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.task = task;
        this.location = location;
        this.date = date;
        this.time = time;
        this.groupSize = groupSize;
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

    public String getTime() {
        return time;
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

    public void setTime(String time) {
        this.time = time;
    }

    public void setGroupSize(String groupSize) {
        this.groupSize = groupSize;
    }
}
