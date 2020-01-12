package com.example.bartertradeapp.DataModels;


public class RatingModel {

    private String feedback,sellerid,buyerid,currentDateTime;
    private int rating,avg_rating;


    // class constructor..
    public RatingModel(){
    }

    // Setter and Getter
    public int getAvg_rating() {
        return avg_rating;
    }
    public void setAvg_rating(int avg_rating) {
        this.avg_rating = avg_rating;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
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
