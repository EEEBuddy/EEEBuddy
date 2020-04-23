package com.example.EEEBuddy;

public class UserInfo {

    private String name;
    private String email;
    private String course;
    private String year;
    private String matric;
    private String profileImageUrl;
    private String hall, gender;
    private String seniorBuddy;

    public UserInfo(String name, String email, String course, String year, String matric, String profileImageUrl, String hall, String gender, String seniorBuddy) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.year = year;
        this.matric = matric;
        this.profileImageUrl = profileImageUrl;
        this.hall = hall;
        this.gender = gender;
        this.seniorBuddy =  seniorBuddy;
    }

    public UserInfo(){

    }

    public String getSeniorBuddy() {
        return seniorBuddy;
    }

    public UserInfo setSeniorBuddy(String seniorBuddy) {
        this.seniorBuddy = seniorBuddy;
        return null;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getMatric() {
        return matric;
    }

    public void setMatric(String matric) {
        this.matric = matric;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {

        this.year = year;
    }


    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
