package com.example.farshad.smarthome.Services;

import com.example.farshad.smarthome.Model.DoorModel;
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

public interface DoorModelService {
    @GET("door/getDoors")
    Call<List<DoorModel>> getDoors();

    @GET("door/{userType}/{familyId}")
    Call<List<DoorModel>> getDoors(@Path("userType") int userType, @Path("familyId") int familyId);

    @GET("door/{doorid}")
    Call<DoorModel> getDoorById(@Path("doorid") String doorId);

    @PUT("door/{doorid}")
    Call<DoorModel> updateDoorById(@Path("doorid") int doorId,@Body DoorModel doorModel);

    @DELETE("door/{doorid}")
    Call<Boolean> deleteDoorById(@Path("doorid") String doorId);

    @POST("door/addDoor")
    Call<Boolean> addNewDoor(@Body DoorModel doorModel);

    @Multipart
    @POST("door/image")
    Call<DoorModel> uploadDoorImage(@Header("Authorization") String authHeader, @PartMap Map<String, RequestBody> map);
}
