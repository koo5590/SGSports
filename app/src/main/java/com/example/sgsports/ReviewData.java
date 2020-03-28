package com.example.sgsports;

public class ReviewData {
    private String user;
    private String facilityName;
    private String facilityType;
    private String review;
    private int rating;

    public ReviewData(String user, String facilityName, String facilityType, String review, int rating){
        this.user = user;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.review = review;
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public String getReview() {
        return review;
    }

    public int getRating(){return rating;}

    public void setUser(String user) {
        this.user = user;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public void setReview(String review) {
        this.review = review;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
}
