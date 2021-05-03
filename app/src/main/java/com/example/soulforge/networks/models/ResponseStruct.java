package com.example.soulforge.networks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseStruct {
    @SerializedName("data")
    @Expose
    private String data;

    @SerializedName("success")
    @Expose
    private SuccessStruct successStruct;

    @SerializedName("error")
    @Expose
    private ErrorStruct errorStruct;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public SuccessStruct getSuccessStruct() {
        return successStruct;
    }

    public void setSuccessStruct(SuccessStruct successStruct) {
        this.successStruct = successStruct;
    }

    public ErrorStruct getErrorStruct() {
        return errorStruct;
    }

    public void setErrorStruct(ErrorStruct errorStruct) {
        this.errorStruct = errorStruct;
    }
}
