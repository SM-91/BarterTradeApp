package com.example.bartertradeapp.DataModels;


public class RatingModel {

        private String feedback;
        private int rating;
        private int avg_rating;

    public int getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(int avg_rating) {
        this.avg_rating = avg_rating;
    }

    private String sellerid;
        private String buyerid;
        private String currentDateTime;

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public RatingModel(){

    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid;
    }

    public String getBuyerid() {
        return buyerid;
    }

    public void setBuyerid(String buyerid) {
        this.buyerid = buyerid;
    }




}
