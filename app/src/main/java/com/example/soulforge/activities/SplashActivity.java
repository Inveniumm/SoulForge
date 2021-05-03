package com.example.soulforge.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.soulforge.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, ReplacerActivity.class));
            finish();
            if(user == null){
                startActivity(new Intent(SplashActivity.this, ReplacerActivity.class));
            }else{
                startActivity(new Intent(SplashActivity.this, ReplacerActivity.class));
            }

        }, 2500 );
    }
}

