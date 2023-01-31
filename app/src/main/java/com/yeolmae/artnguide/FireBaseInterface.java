package com.yeolmae.artnguide;
import retrofit2.Call;
//import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FireBaseInterface {
    @Headers({"Accept:*/*", "Content-Type: application/x-www-form-urlencoded"})
    @POST("device")
    @FormUrlEncoded
    Call<FireBaseModel> saveFireBase(@Field("email") String email,@Field("deviceToken") String deviceToken);
}
