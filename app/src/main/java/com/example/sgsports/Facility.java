package com.example.sgsports;

import java.io.Serializable;

public class Facility implements Serializable {

    private String Name;
    private Double Latitude;
    private Double Longitude;
    private String Description;
    private String Type;
    private String Address;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public Facility(String name, String Desc, String Type, String Address){
        this.Name = name;
        this.Description = Desc;
        this.Type = Type;
        this.Address = Address;
    }

    public String getDescription() {
        return Description;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Facility() {
    }

    /*** getter ***/
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getLatitude() {
        return Latitude;
    }


    /*** setter ***/
    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

}
