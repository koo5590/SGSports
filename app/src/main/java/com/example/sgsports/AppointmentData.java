package com.example.sgsports;

public class AppointmentData {
    private String user;
    private String date;
    private String timeslot;

    public AppointmentData(String user, String date, String timeslot){
        this.user = user;
        this.date = date;
        this.timeslot = timeslot;
    }

    public String getUser() {
        return user;
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

    public void setDate(String date) {
        this.date = date;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }
}


