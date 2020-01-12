package com.example.bartertradeapp.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class UserModel implements Parcelable {

    private String userId;
    private String userName;
    private String userEmail;
    private String userAge;
    private String userGender;
    private String userBio;
    private String userImageUrl;

    private ArrayList<String> userInterests;

    public UserModel(){}

    protected UserModel(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        userEmail = in.readString();
        userAge = in.readString();
        userGender = in.readString();
        userBio = in.readString();
        userImageUrl = in.readString();
        userInterests = in.createStringArrayList();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userEmail);
        dest.writeString(userAge);
        dest.writeString(userGender);
        dest.writeString(userBio);
        dest.writeString(userImageUrl);
        dest.writeStringList(userInterests);
    }
}
