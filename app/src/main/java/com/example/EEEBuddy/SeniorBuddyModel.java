package com.example.EEEBuddy;

public class SeniorBuddyModel {
    private String name;
    private String email;
    private String course;
    private String year;
    private String profileImageUrl;
    private String experience;
    private String selfIntro;

    public SeniorBuddyModel(){

    }

    public SeniorBuddyModel(String name, String email, String course, String year, String profileImageUrl, String experience, String selfIntro) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.year = year;
        this.profileImageUrl = profileImageUrl;
        this.experience = experience;
        this.selfIntro = selfIntro;
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

    public void setExperience(String experience) {
        this.experience = experience;
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

    public String getExperience() {
        return experience;
    }

    public String getSelfIntro() {
        return selfIntro;
    }
}
