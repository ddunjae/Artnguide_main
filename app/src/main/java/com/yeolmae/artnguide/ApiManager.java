package com.yeolmae.artnguide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    private static FireBaseInterface fireBaseInterface;
    private static ApiManager apiManager;

    private ApiManager(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.devartnguide.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        fireBaseInterface = retrofit.create(FireBaseInterface.class);
    }

    public static ApiManager getInstance() {
        if (apiManager == null) {
            apiManager = new ApiManager();
        }
        return apiManager;
    }

    public void saveFirebase(FireBaseModel fireBaseModel, Callback<FireBaseModel> callback) {
        Call<FireBaseModel> userCall = fireBaseInterface.saveFireBase(fireBaseModel.getEmail(),fireBaseModel.getFcmToken());
        userCall.enqueue(callback);
    }
}