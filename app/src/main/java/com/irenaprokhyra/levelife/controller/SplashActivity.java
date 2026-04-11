package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.util.SessionManager;

public class SplashActivity extends AppCompatActivity {
    private MainRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        repository = MainRepository.getInstance(getApplication());

        new Handler(Looper.getMainLooper()).postDelayed(this::routeUser, 3400);
    }

    private void routeUser() {
        int savedUserId = SessionManager.getSavedUserId(this);
        if (savedUserId == -1) {
            openLogin();
            return;
        }

        repository.getUserById(savedUserId, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("USER_ID", user.getId());
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                SessionManager.clearSession(SplashActivity.this);
                openLogin();
            }
        });
    }

    private void openLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
