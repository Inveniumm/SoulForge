package com.example.soulforge.utils;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soulforge.MyApplication;
import com.google.gson.Gson;

public class BaseActivity extends AppCompatActivity {
    public Activity mAct = BaseActivity.this;
    public MyApplication app;
    public SingletonClass singleton;
    public Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBaseActivity();
    }

    private void initBaseActivity() {
        gson = new Gson();
        app = (MyApplication) getApplication();
        singleton = SingletonClass.getInstance();
        singleton.setCurrentActivity(mAct);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        singleton.setCurrentActivity(mAct);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
