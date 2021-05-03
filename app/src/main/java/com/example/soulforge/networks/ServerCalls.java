package com.example.soulforge.networks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.soulforge.interfaces.OnServerResCallBack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerCalls extends ParseJSON {
    public void makePostCall(Call<Object> call, final OnServerResCallBack callBack) {
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseStr = ApiClient.getGSONBuilder().toJson(response.body());
                    OnServerResCallBack verifyServerResCallBack = (status, message, jsonResponse) -> callBack.onResponse(status, message, jsonResponse);
                    verifyServerResponse(responseStr, verifyServerResCallBack);
                    Log.e("ResponseSuccess", responseStr);
                } else {
                    Log.e("ResponseError", response.message());
                    callBack.onResponse(UNKNOWN, null, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                callBack.onResponse(ERROR, t.getMessage(), null);
                Log.e("ResponseError", t.getMessage());
            }
        });
    }
}