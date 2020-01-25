package com.example.bartertradeapp.DataModels;

import java.util.ArrayList;
import java.util.HashMap;

public class UserUploadProductModel {

    private String adId;
    private String productName;
    private String productType; // either used or new
    private String productDescription;
    private String productCondition; // Either good/normal
    private String productEstimatedMarketValue;
    private String possibleExchangeWith;
    private String productCategoryList;
    private String serviceName;
    private String serviceCategory;
    private String serviceDescription;
    private String serviceEstimatedMarketValue;
    private String servicePossibleExchangeWith;
    private String serviceImageUri;
    private double latitude;
    private double longitude;
    private String currentDateTime;
    private UserModel postedBy;
    private String tag;



    private ArrayList<String> mArrList;
    private String mImageUri;

    public UserUploadProductModel() {
    }

    public UserUploadProductModel(String productName, String productDescription, String productCondition, String mImageUri) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productCondition = productCondition;
        this.mImageUri = mImageUri;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCondition() {
        return productCondition;
    }

    public void setProductCondition(String productCondition) {
        this.productCondition = productCondition;
    }

    public String getProductEstimatedMarketValue() {
        return productEstimatedMarketValue;
    }

    public void setProductEstimatedMarketValue(String productEstimatedMarketValue) {
        this.productEstimatedMarketValue = productEstimatedMarketValue;
    }

    public String getPossibleExchangeWith() {
        return possibleExchangeWith;
    }

    public void setPossibleExchangeWith(String possibleExchangeWith) {
        this.possibleExchangeWith = possibleExchangeWith;
    }

    public String getProductCategoryList() {
        return productCategoryList;
    }

    public void setProductCategoryList(String productCategoryList) {
        this.productCategoryList = productCategoryList;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public ArrayList<String> getmArrList() {
        return mArrList;
    }

    public void setmArrList(ArrayList<String> mArrList) {
        this.mArrList = mArrList;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public UserModel getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(UserModel postedBy) {
        this.postedBy = postedBy;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceEstimatedMarketValue() {
        return serviceEstimatedMarketValue;
    }

    public void setServiceEstimatedMarketValue(String serviceEstimatedMarketValue) {
        this.serviceEstimatedMarketValue = serviceEstimatedMarketValue;
    }

    public String getServicePossibleExchangeWith() {
        return servicePossibleExchangeWith;
    }

    public void setServicePossibleExchangeWith(String servicePossibleExchangeWith) {
        this.servicePossibleExchangeWith = servicePossibleExchangeWith;
    }

    public String getServiceImageUri() {
        return serviceImageUri;
    }

    public void setServiceImageUri(String serviceImageUri) {
        this.serviceImageUri = serviceImageUri;
    }
}
