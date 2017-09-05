package com.example.farshad.smarthome.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Farshad on 5/12/2017.
 */

public class UserModel {
    private int userid;
    private String usertype;
    private String name;
    private String lastname;
    private String username;
    private String password;
    @SerializedName("image")
    public String imageUrl;
    private int familyId;

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public int getFamilyId() {
        return familyId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int id) {
        this.userid = id;
    }

    public String getUserType() {
        return usertype;
    }

    public void setUserType(String userType) {
        this.usertype = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserModel(){

    }

    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;
    }



}
