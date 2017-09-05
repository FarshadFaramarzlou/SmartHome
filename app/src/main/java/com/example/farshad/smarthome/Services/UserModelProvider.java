package com.example.farshad.smarthome.Services;

import com.example.farshad.smarthome.Model.RefreshTokenRequestModel;
import com.example.farshad.smarthome.Model.TokenModel;
import com.example.farshad.smarthome.Model.UserModel;
import com.example.farshad.smarthome.TApplication;
import com.example.farshad.smarthome.Utility.AppPreferenceTools;
import com.example.farshad.smarthome.Utility.ClientConfigs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Date;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Farshad on 5/12/2017.
 */

public class UserModelProvider {
    private UserModelService uMService;
    private Retrofit mRetrofitClient;
    private AppPreferenceTools mAppPreferenceTools;

    //config retrofit
    public UserModelProvider(){
        this.mAppPreferenceTools = new AppPreferenceTools(TApplication.applicationContext);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //OkHttpClient httpClient = new OkHttpClient();


        //add http interceptor to add headers to each request
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                String originalPath = original.url().url().getPath();
                if (originalPath.endsWith("user/profile/image") || originalPath.endsWith("start/refreshToken")||originalPath.endsWith("user/addUser")) {
                    return chain.proceed(original);
                } else {
                    //build request
                    Request.Builder requestBuilder = original.newBuilder();
                    //add header for all of the request
                    requestBuilder.addHeader("Accept", "application/json");
                    //check is user logged in , if yes should add authorization header to every request
                    if (mAppPreferenceTools.isAuthorized()) {
                        requestBuilder.addHeader("Authorization", mAppPreferenceTools.getAccessToken());
                    }
                    requestBuilder.method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            }
        });



        //when request get 401 http error code this method run and get refreshToken and send original request again
        httpClient.authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                if (mAppPreferenceTools.isAuthorized()) {
                    //make the refresh token request model
                    RefreshTokenRequestModel requestModel = new RefreshTokenRequestModel();
                    requestModel.refresh_token = mAppPreferenceTools.getRefreshToken();
                    //make call
                    Call<TokenModel> call = uMService.getRefreshToken(requestModel);
                    retrofit2.Response<TokenModel> tokenModelResponse = call.execute();
                    if (tokenModelResponse.isSuccessful()) {
                        mAppPreferenceTools.saveTokenModel(tokenModelResponse.body());
                        return response.request().newBuilder()
                                .removeHeader("Authorization")
                                .addHeader("Authorization", "Basic " + mAppPreferenceTools.getAccessToken())
                                .build();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        });


        //create new gson object to define custom converter on Date type
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new UTCDateTypeAdapter())
                .create();

        mRetrofitClient = new Retrofit.Builder()
                .baseUrl(ClientConfigs.REST_API_BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        uMService = mRetrofitClient.create(UserModelService.class);
    }

    public  UserModelService getUService(){return uMService;}

}
