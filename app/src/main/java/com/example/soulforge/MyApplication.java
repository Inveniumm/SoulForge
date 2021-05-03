package com.example.soulforge;

import android.content.Context;
import android.os.StrictMode;

import androidx.multidex.MultiDex;

import com.example.soulforge.utils.SingletonClass;

public class MyApplication extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();

        SingletonClass.getInstance();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }
}
