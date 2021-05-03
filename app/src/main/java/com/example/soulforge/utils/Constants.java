package com.example.soulforge.utils;

// Its global variable class
public class Constants {
    public static final String PREF_NAME = "pref_name", PREF_STORED = "perf_store", PREF_URL = "pref_url", PREF_DIRECTORY = "pref_direct";
    public static String USER_ID;
    public static boolean IS_SEARCHED_USER = false;
    public static boolean IS_FROM_HOME = false;
    public static boolean IS_FROM_NOTIFICATION = false;
    public static final String APP_SHARED_PREFERENCE = "FIVER_APP_SHARED_PREFERENCE";
    public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
    public static  String notificationToken = "";

    public final int UNKNOWN = -1;
    public final int ERROR = 0;
    public final int SUCCESS = 1;
    public final int WARNING = 2;
    public final int DEFAULT = 3;

    public final int DEFAULT_TOAST = 0;
    public final int TOAST_ERROR = 1;
    public final int TOAST_CONFIRMED = 2;
    public final int TOAST_INFO = 3;

    public final int START_ACTIVITY = 0;
    public final int START_ACTIVITY_WITH_FINISH = 1;
    public final int START_ACTIVITY_WITH_CLEAR_BACK_STACK = 2;
    public final int START_ACTIVITY_WITH_TOP = 3;
    public final int FINISH_CURRENT_ACTIVITY = 4;

    public final int LENGTH_SHORT = 0;
    public final int LENGTH_LONG = 1;

    public final int PASSWORD_MIN_LENGTH = 6;
}
