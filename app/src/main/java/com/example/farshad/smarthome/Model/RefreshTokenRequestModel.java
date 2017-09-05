package com.example.farshad.smarthome.Model;


import com.example.farshad.smarthome.Utility.ClientConfigs;

/**
 * used in refresh token request
 */
public class RefreshTokenRequestModel {
    public String client_id;
    public String client_key;
    public String refresh_token;

    public RefreshTokenRequestModel() {
        this.client_id = ClientConfigs.CLIENT_ID;
        this.client_key = ClientConfigs.CLIENT_KEY;
    }
}
