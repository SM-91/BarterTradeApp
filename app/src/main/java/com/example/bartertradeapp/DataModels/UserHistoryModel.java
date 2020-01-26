package com.example.bartertradeapp.DataModels;

public class UserHistoryModel {

    private UserModel sender;
    private UserModel reciever;
    private String category;
    private String tag;
    private String dateAndTime;
    private String conversationID;
    private String name;

    public UserModel getSender() {
        return sender;
    }

    public void setSender(UserModel sender) {
        this.sender = sender;
    }

    public UserModel getReciever() {
        return reciever;
    }

    public void setReciever(UserModel reciever) {
        this.reciever = reciever;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
