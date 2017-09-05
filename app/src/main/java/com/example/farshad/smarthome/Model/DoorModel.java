package com.example.farshad.smarthome.Model;

import com.example.farshad.smarthome.R;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Farshad on 7/11/2017.
 */

public class DoorModel {

    @SerializedName("image")
    private int DoorImageID;
    private int doorId;
    private int familyId;
    private String doorName;
    private String doorDes;


    public int getDoorImageID() {
        return DoorImageID;
    }

    public void setDoorImageID(int doorImageID) {
        this.DoorImageID = doorImageID;
    }

    public int getDoorId() {
        return doorId;
    }

    public void setDoorId(int doorId) {
        this.doorId = doorId;
    }

    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    public String getDoorDes() {
        return doorDes;
    }

    public void setDoorDes(String doorDes) {
        this.doorDes = doorDes;
    }






    public static int[] getImages() {
        int[] images = {
                R.drawable.common_google_signin_btn_text_dark_disabled,R.drawable.common_google_signin_btn_text_dark_disabled
        };
        return images;
    }
}