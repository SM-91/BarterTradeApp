package com.example.bartertradeapp.DataModels;

public class CustomModel {

    UserModel userModel;
    UserUploadProductModel userUploadProductModel;
    ChatModel chatModel;
    RequestModel requestModel;
    UserHistoryModel userHistoryModel;
    RatingModel ratingModel;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public UserUploadProductModel getUserUploadProductModel() {
        return userUploadProductModel;
    }

    public void setUserUploadProductModel(UserUploadProductModel userUploadProductModel) {
        this.userUploadProductModel = userUploadProductModel;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public RequestModel getRequestModel() {
        return requestModel;
    }

    public void setRequestModel(RequestModel requestModel) {
        this.requestModel = requestModel;
    }

    public UserHistoryModel getUserHistoryModel() {
        return userHistoryModel;
    }

    public void setUserHistoryModel(UserHistoryModel userHistoryModel) {
        this.userHistoryModel = userHistoryModel;
    }

    public RatingModel getRatingModel() {
        return ratingModel;
    }

    public void setRatingModel(RatingModel ratingModel) {
        this.ratingModel = ratingModel;
    }
}
