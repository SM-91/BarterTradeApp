package com.example.bartertradeapp.DataModels;

import java.util.ArrayList;

public class UserModel {

    private String userName;
    private String userEmail;
    private String userAge;
    private String userGender;
    private String userBio;
    private String userImageUrl;

    private ArrayList<String> userInterests;

    public UserModel(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public ArrayList<String> getUserInterests() {
        return userInterests;
    }

    public void setUserInterests(ArrayList<String> userInterests) {
        this.userInterests = userInterests;
    }

}
