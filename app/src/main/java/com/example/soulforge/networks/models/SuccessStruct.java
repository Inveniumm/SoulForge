package com.example.soulforge.networks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuccessStruct {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("token")
    @Expose
    private String token;
}