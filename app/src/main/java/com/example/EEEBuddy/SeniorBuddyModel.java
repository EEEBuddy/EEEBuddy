package com.example.EEEBuddy;

public class SeniorBuddyModel {
    private String name;
    private String email;
    private String course;
    private String year;
    private String profileImageUrl;
    private String dateJoin;
    private String selfIntro;
    private String gender;
    private String hall;

    public SeniorBuddyModel(){

    }

    public SeniorBuddyModel(String name, String email, String course, String year, String profileImageUrl, String dateJoin, String selfIntro, String hall, String gender) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.year = year;
        this.profileImageUrl = profileImageUrl;
        this.dateJoin = dateJoin;
        this.selfIntro = selfIntro;
        this.hall = hall;
        this.gender = gender;
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

    public void setDateJoin(String experience) {
        this.dateJoin = dateJoin;
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

    public String getDateJoin() {
        return dateJoin;
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
}
