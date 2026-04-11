package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private MainRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = MainRepository.getInstance(getApplication());

        checkSession();

        setContentView(R.layout.activity_login);
        initViews();
        setupListeners();
    }

    private void checkSession() {
        int savedUserId = SessionManager.getSavedUserId(this);
        if (savedUserId == -1) {
            return;
        }

        repository.getUserById(savedUserId, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                navigateToMain(user.getId());
            }

            @Override
            public void onError(String message) {
                SessionManager.clearSession(LoginActivity.this);
                runOnUiThread(() -> Toast.makeText(
                        LoginActivity.this,
                        getString(R.string.error_session_expired),
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(username, password)) {
                performLogin(username, password);
            }
        });

        tvRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(username, password)) {
                performRegister(username, password);
            }
        });
    }

    private boolean validateInput(String user, String password) {
        if (user.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performLogin(String username, String password) {
        repository.loginUser(username, password, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    SessionManager.saveSession(LoginActivity.this, user.getId());
                    String welcome = getString(R.string.welcome_message, user.getName());
                    Toast.makeText(LoginActivity.this, welcome, Toast.LENGTH_SHORT).show();
                    navigateToMain(user.getId());
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(
                        LoginActivity.this,
                        getString(R.string.auth_error_login_failed),
                        Toast.LENGTH_SHORT
                ).show());
            }
        });
    }

    private void navigateToMain(int userId) {
        runOnUiThread(() -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
        });
    }

    private void performRegister(String username, String password) {
        repository.registerUser(username, password, new MainRepository.RegistrationCallback() {
            @Override
            public void onSuccess(int userId) {
                runOnUiThread(() -> {
                    SessionManager.saveSession(LoginActivity.this, userId);
                    String welcome = getString(R.string.welcome_message, username);
                    Toast.makeText(LoginActivity.this, welcome, Toast.LENGTH_SHORT).show();
                    navigateToMain(userId);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
