package com.example.bartertradeapp.DataModels;

public class RequestModel {

    private String adId;
    private String requestId;
    private UserModel sender;
    private UserModel reciever;
    private String name;
    private String tag;
    private boolean accepted;

    public RequestModel() {
    }

    public RequestModel(String adId, UserModel sender, UserModel reciever, String name, boolean accepted) {
        this.adId = adId;
        this.sender = sender;
        this.reciever = reciever;
        this.name = name;
        this.accepted = accepted;
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

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
