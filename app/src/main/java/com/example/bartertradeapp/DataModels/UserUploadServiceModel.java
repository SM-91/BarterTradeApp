package com.example.bartertradeapp.DataModels;

public class UserUploadServiceModel {

    private String uid;
    private String serviceName;
    private String serviceCategory;
    private String serviceDescription;
    private String serviceEstimatedMarketValue;
    private String servicePossibleExchangeWith;

    public UserUploadServiceModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}
