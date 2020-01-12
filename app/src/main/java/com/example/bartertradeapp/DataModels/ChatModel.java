package com.example.bartertradeapp.DataModels;

public class ChatModel {

    private UserModel sender;
    private UserModel reciever;
    private String message;

    public ChatModel(UserModel sender, UserModel reciever, String message) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
    }

    public ChatModel(){

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
