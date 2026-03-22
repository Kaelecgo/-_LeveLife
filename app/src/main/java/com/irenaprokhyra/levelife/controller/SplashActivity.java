package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.irenaprokhyra.levelife.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::routeUser, 3400);
    }

    private void routeUser() {
        SharedPreferences prefs = getSharedPreferences("LeveLifeSession", MODE_PRIVATE);
        int savedUserId = prefs.getInt("USER_ID", -1);

        Intent intent;
        if (savedUserId != -1) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("USER_ID", savedUserId);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}