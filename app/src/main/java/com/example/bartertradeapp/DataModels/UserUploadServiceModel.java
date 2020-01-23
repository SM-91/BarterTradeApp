package com.example.bartertradeapp.DataModels;

public class UserUploadServiceModel {

    private String ad_id;
    private String serviceName;
    private String serviceCategory;
    private String serviceDescription;
    private String serviceEstimatedMarketValue;
    private String servicePossibleExchangeWith;
    private double latitude;
    private double longitude;
    private String currentDateTime;
    private UserModel postedBy;
    private String tag;
    private String mImageUrl;

    public UserUploadServiceModel() {
    }

    public String getAd_id() {
        return ad_id;
    }

    public void setAd_id(String ad_id) {
        this.ad_id = ad_id;
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

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
