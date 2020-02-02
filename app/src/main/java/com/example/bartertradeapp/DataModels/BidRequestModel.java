package com.example.bartertradeapp.DataModels;

public class BidRequestModel {
    private UserModel userModel;
    private RequestModel requestModel;
    private UserUploadProductModel productModel;
    private ChatModel chatModel;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public RequestModel getRequestModel() {
        return requestModel;
    }

    public void setRequestModel(RequestModel requestModel) {
        this.requestModel = requestModel;
    }

    public UserUploadProductModel getProductModel() {
        return productModel;
    }

    public void setProductModel(UserUploadProductModel productModel) {
        this.productModel = productModel;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /*
if(    isAccpted = false)

    screen pe text nazar aa rha hai wo hga sara black
    else if(isAccpted = true)
    screen pe text is gray
     */


}
