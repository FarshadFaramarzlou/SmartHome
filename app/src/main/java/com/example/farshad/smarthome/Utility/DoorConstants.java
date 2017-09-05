package com.example.farshad.smarthome.Utility;

/**
 * Created by Farshad on 7/17/2017.
 */

public class DoorConstants {
    //request codes
    public static final int CREATE_OR_EDIT_DOOR_REQUEST_CODE = 7;
    public static final int UPDATE_DOOR_PROFILE_REQUEST_CODE = 8;
    public static final int GALLERY_REQUEST_CODE = 12;
    public static final int FOR_OPEN_GALLERY_REQUEST_WRITE_EXTERNAL_STORAGE_PER = 13;

    //keys
    public static final String ACTION_TO_DO_KEY="Action_TO_DO_KEY";
    public static final String DOOR_ID_KEY="DOOR_ID_KEY";

    //type of actions
    public static final int DO_NOTHING = 0;
    public static final int UPDATEE_FROM_NET=1;
    public static final int EDIT_DOOR=2;
    public static final int NEW_DOOR =3;

}
