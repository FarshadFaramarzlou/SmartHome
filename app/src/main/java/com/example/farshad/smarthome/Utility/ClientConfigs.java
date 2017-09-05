package com.example.farshad.smarthome.Utility;


import com.example.farshad.smarthome.R;

/**
 * Created by Farshad on 5/12/2017.
 */

public class ClientConfigs {
    public  static String IP_ADDRESS = "2.187.150.247";
    public  static String PORT= "8080";
    public  static String projectName="smartHome1";

    public static final String REST_API_BASE_URL = "http://"+IP_ADDRESS+":"+PORT+"/"+projectName+"/webapi/";

    //TODO: create new Client with postman in http://localshot:/api/v1/client with body {"name":"android client app"} and set these values with client_id and client_key
    public static final String CLIENT_ID = "";
    public static final String CLIENT_KEY = "";
}
