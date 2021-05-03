package com.example.soulforge.networks;

import com.example.soulforge.interfaces.OnServerResCallBack;
import com.example.soulforge.utils.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ParseJSON extends PrefManager {
    public void verifyServerResponse(String responseStr, OnServerResCallBack callBack) {
        try {
            JSONObject jsonResponse = new JSONObject(responseStr);
            JSONObject successObj = jsonResponse.optJSONObject("success");
            JSONObject errorObj = jsonResponse.optJSONObject("error");
            String messageStr = "";

            if (successObj != null) {
                messageStr = successObj.optString("message", "");
                callBack.onResponse(SUCCESS, messageStr, successObj.toString());
            } else if (errorObj != null) {
                messageStr = errorObj.optString("message", "");
                callBack.onResponse(ERROR, messageStr, errorObj.toString());
            } else {
                callBack.onResponse(UNKNOWN, messageStr, jsonResponse.toString());
            }
        } catch (JSONException e) {
            callBack.onResponse(UNKNOWN, e.getMessage(), null);
            e.printStackTrace();
        }
    }

    public String getValueStrFromJSON(String resStr, String key) {
        String value = "";
        JSONObject jsonRes = null;
        try {
            jsonRes = new JSONObject(resStr);
            value = jsonRes.optString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }
}
