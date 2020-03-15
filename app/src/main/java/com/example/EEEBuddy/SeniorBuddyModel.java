package com.example.EEEBuddy;

public class SeniorBuddyModel {
    private String name;
    private String email;
    private String course;
    private String year;
    private String profileImageUrl;
    private String joinedDate;
    private String selfIntro;
    private String gender;
    private String hall;
    private String applicationDate;
    private  String relationDate;

    public SeniorBuddyModel(){

    }

    public SeniorBuddyModel(String name, String email, String course, String year, String profileImageUrl, String joinedDate, String selfIntro, String hall, String gender) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.year = year;
        this.profileImageUrl = profileImageUrl;
        this.joinedDate = joinedDate;
        this.selfIntro = selfIntro;
        this.hall = hall;
        this.gender = gender;
    }

    public SeniorBuddyModel(String selfIntro, String applicationDate){
        this.joinedDate = applicationDate;
        this.selfIntro = selfIntro;
    }

    public SeniorBuddyModel(String relationDate){
        this.relationDate = relationDate;
    }


    public String getRelationDate() {
        return relationDate;
    }

    public void setRelationDate(String relationDate) {
        this.relationDate = relationDate;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


    public void setSelfIntro(String selfIntro) {
        this.selfIntro = selfIntro;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public String getYear() {
        return year;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }



    public String getSelfIntro() {
        return selfIntro;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }


}
