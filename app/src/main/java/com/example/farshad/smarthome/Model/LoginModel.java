package com.example.farshad.smarthome.Model;


import com.example.farshad.smarthome.Utility.ClientConfigs;

/**
 * Sgin in Requst model used in sign in request
 */
public class LoginModel {
    public String client_id;
    public String client_key;
    public String email;
    public String password;

    public LoginModel() {
        this.client_id = ClientConfigs.CLIENT_ID;
        this.client_key = ClientConfigs.CLIENT_KEY;
    }
}
