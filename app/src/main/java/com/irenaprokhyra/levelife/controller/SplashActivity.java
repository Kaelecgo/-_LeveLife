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

        // Retardo para mostrar el logo de la aplicación
        new Handler(Looper.getMainLooper()).postDelayed(this::routeUser, 3400);
    }

    private void routeUser() {
        SharedPreferences prefs = getSharedPreferences("LeveLifeSession", MODE_PRIVATE);
        
        // CORRECCIÓN: Usar la misma clave que LoginActivity ("saved_user_id")
        int savedUserId = prefs.getInt("saved_user_id", -1);

        Intent intent;
        if (savedUserId != -1) {
            // Si hay sesión guardada, vamos directo al Home pasándole el ID
            intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("USER_ID", savedUserId);
        } else {
            // Si no hay sesión, vamos a la pantalla de Login
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
