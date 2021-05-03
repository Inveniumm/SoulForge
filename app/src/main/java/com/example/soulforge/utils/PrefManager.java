package com.example.soulforge.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static com.example.soulforge.utils.Constants.APP_SHARED_PREFERENCE;

public class PrefManager extends Methods{
    public static SharedPreferences getPrefs(Activity mAct) {
        return mAct.getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static int getMyIntPref(Activity mAct, String prefName, int defVal) {
        return getPrefs(mAct).getInt(prefName, defVal);
    }

    public static void setMyIntPref(Activity mAct, String prefName, int value) {
        getPrefs(mAct).edit().putInt(prefName, value).apply();
    }

    public static String getMyStringPref(Activity mAct, String prefName, String defVal) {
        return getPrefs(mAct).getString(prefName, defVal);
    }

    public static void setMyStringPref(Activity mAct, String prefName, String value) {
        getPrefs(mAct).edit().putString(prefName, value).apply();
    }

    public static boolean getMyBooleanPref(Activity mAct, String prefName, boolean defVal) {
        return getPrefs(mAct).getBoolean(prefName, defVal);
    }

    public static void setMyBooleanPref(Activity mAct, String prefName, boolean value) {
        getPrefs(mAct).edit().putBoolean(prefName, value).apply();
    }
}