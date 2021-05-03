package com.example.soulforge.utils;


import com.example.soulforge.networks.ApiClient;
import com.example.soulforge.networks.ApiInterface;
import com.example.soulforge.networks.ServerCalls;

public class SingletonClass extends ServerCalls {
    private static SingletonClass sSoleInstance;
    private ApiInterface apiInterface;

    private SingletonClass() {
    }

    public static SingletonClass getInstance() {
        if (sSoleInstance == null) {
            sSoleInstance = new SingletonClass();
        }

        return sSoleInstance;
    }

    public ApiInterface getApiInterface() {
        if (apiInterface == null)
            apiInterface = ApiClient.getClient().create(ApiInterface.class);
        return apiInterface;
    }
}
