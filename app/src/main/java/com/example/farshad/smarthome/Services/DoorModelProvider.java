package com.example.farshad.smarthome.Services;

import com.example.farshad.smarthome.Utility.ClientConfigs;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Farshad on 5/12/2017.
 */

public class DoorModelProvider {
    private DoorModelService dMService;

    //config retrofit
    public DoorModelProvider(){
        OkHttpClient httpClient = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ClientConfigs.REST_API_BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dMService = retrofit.create(DoorModelService.class);
    }

    public  DoorModelService getDService(){return dMService;}

}
