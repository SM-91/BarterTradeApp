package com.example.bartertradeapp.DataModels;

import com.google.firebase.firestore.auth.User;

public class ChatModel {

    private UserModel sender;
    private UserModel reciever;
    private String message;
    private String conversationID;
    private String name;

    public ChatModel(String ad_id, String name, UserModel sender, UserModel reciever, String message) {

        this.conversationID = ad_id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
