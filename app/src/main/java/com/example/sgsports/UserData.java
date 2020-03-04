package com.example.sgsports;

/** contains info of user **/
public class UserData {
    private String username;
    private String useremail;
    private String gender;
    private Integer age;
    private String explanation;

    /** constructor **/
    public UserData(String username, String useremail, String gender, Integer age){
        this.username = username;
        this.useremail = useremail;
        this.gender = gender;
        this.age = age;
        this.explanation = null;
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
}
