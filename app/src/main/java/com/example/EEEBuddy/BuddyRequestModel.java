package com.example.EEEBuddy;

public class BuddyRequestModel {

    public String request_type;


    public BuddyRequestModel(){

    }

    public BuddyRequestModel(String request_type){
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
