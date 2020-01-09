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
}
