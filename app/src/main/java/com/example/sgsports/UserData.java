package com.example.sgsports;

import java.io.Serializable;

/** contains info of user **/
public class UserData implements Serializable {
    private String username;
    private String useremail;
    private String gender;
    private Integer age;
    private String explanation;
    private String mobilenum;

    /** constructor **/
    public UserData(String username, String useremail, String gender, Integer age, String mobilenum){
        this.username = username;
        this.useremail = useremail;
        this.gender = gender;
        this.age = age;
        this.explanation = null;
        this.mobilenum = mobilenum;
    }

    /** getter **/
    public String getUsername() {
        return username;
    }

    public String getUseremail() {
        return useremail;
    }

    public String isGender() {
        return gender;
    }

    public Integer getAge() {
        return age;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getMobilenum() { return mobilenum; }

    /** setter **/
    public void setUsername(String username) {
        this.username = username;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setMobilenum(String mobilenum) { this.mobilenum = mobilenum; }
}
