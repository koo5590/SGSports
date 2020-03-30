package com.example.sgsports;

public class AppointmentData {
    private String user;
    private String facilityName;
    private String facilityType;
    private String date;
    private String timeslot;

    public AppointmentData(String user,  String facilityName, String facilityType, String date, String timeslot){
        this.user = user;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.date = date;
        this.timeslot = timeslot;
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

    public String getDate() {
        return date;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }
}


