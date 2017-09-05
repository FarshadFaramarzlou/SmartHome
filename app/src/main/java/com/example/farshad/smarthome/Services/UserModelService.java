package com.example.farshad.smarthome.Services;

import com.example.farshad.smarthome.Model.AuthenticationResponseModel;
import com.example.farshad.smarthome.Model.LoginModel;
import com.example.farshad.smarthome.Model.RefreshTokenRequestModel;
import com.example.farshad.smarthome.Model.TokenModel;
import com.example.farshad.smarthome.Model.UserModel;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * Created by Farshad on 5/12/2017.
 */

public interface UserModelService {
    @POST("login")
    Call<AuthenticationResponseModel> loginUser(@Body LoginModel loginModel);

    @GET("user/{userType}/{familyId}")
    Call<List<UserModel>> getUsers(@Path("userType") int userType,@Path("familyId") int familyId);

    @GET("user/{userid}")
    Call<UserModel> getUserById(@Path("userid") String userid);

    @PUT("user/{userid}")
    Call<UserModel> updateUserById(@Path("userid") int userid,@Body UserModel userModel);

    @DELETE("user/{userid}")
    Call<Boolean> deleteUserById(@Path("userid") String userid);

    @POST("user/addUser")
    Call<Boolean> addNewUser(@Body UserModel userModel);

    @POST("start/refreshToken")
    Call<TokenModel> getRefreshToken(@Body RefreshTokenRequestModel refreshTokenRequestModel);

    @PUT("user/profile")
    Call<UserModel> updateUserProfile(@Body UserModel userModel);

    @Multipart
    @POST("user/profile/image")
    Call<UserModel> uploadUserProfileImage(@Header("Authorization") String authHeader, @PartMap Map<String, RequestBody> map);

    @DELETE("user/terminate")
    Call<Boolean> terminateApp();
}
