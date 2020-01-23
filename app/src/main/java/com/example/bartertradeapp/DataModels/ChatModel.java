package com.example.bartertradeapp.DataModels;

import com.google.firebase.firestore.auth.User;

public class ChatModel {

    private UserModel sender;
    private UserModel reciever;
    private String message;
    private String conversationID;
    private String foodName;

    public ChatModel(String ad_id, String food_name, UserModel sender, UserModel reciever, String message) {

        this.conversationID = ad_id;
        this.foodName = food_name;
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
    }

    public ChatModel(){

    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
