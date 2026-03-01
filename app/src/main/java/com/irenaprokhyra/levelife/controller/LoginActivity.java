package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    // >_ Botón de registro _<
    private Button btnRegister;

    private MainRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // >_ INICIALIZACIÓN DEL REPOSITORIO _<
        repository = MainRepository.getInstance(getApplication());

        checkSession();

        setContentView(R.layout.activity_login);
        initViews();
        setupListeners();
    }

    // >_ METODO AUTO-LOGIN SEGURO _<
    private void checkSession() {
        int savedUserId = getSharedPreferences("LeveLifeSession", MODE_PRIVATE)
                .getInt("saved_user_id", -1);

        if (savedUserId != -1) {
            // Preguntamos a la BBDD si este usuario aún existe
            repository.getUserById(savedUserId, new MainRepository.LoginCallback() {
                @Override
                public void onSuccess(User user) {
                    // Si existe - entramos
                    navigateToMain(user.getId());
                }

                @Override
                public void onError(String message) {
                    // El usuario guardado ya no existe (quizás borramos la BBDD)
                    // Limpiamos la preferencia corrupta y nos quedamos en Login
                    getSharedPreferences("LeveLifeSession", MODE_PRIVATE)
                            .edit().clear().apply();

                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, getString(R.string.error_session_expired), Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(username, password)) {
                performLogin(username, password);
            }
        });
        // >_ LÓGICA DE REGISTRO (Para cuando pongas el botón) _<
        btnRegister.setOnClickListener(v -> {
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
            // >_ GUARDAR SESIÓN _<
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    saveSession(user.getId());
                    String welcome = getString(R.string.welcome_message, user.getName());
                    Toast.makeText(LoginActivity.this, welcome, Toast.LENGTH_SHORT).show();
                    navigateToMain(user.getId());
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // >_ Helper para guardar sesión _<
    private void saveSession(int userId) {
        getSharedPreferences("LeveLifeSession", MODE_PRIVATE)
                .edit()
                .putInt("saved_user_id", userId)
                .apply();
    }

    // >_ Helper para navegar (Evita repetir código) _<
    private void navigateToMain(int userId) {
        runOnUiThread(() -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
        });
    }

    // >_ METODO DE REGISTRO _<
    private void performRegister(String username, String password) {
        // Primero verificamos si ya existe el nombre
        repository.checkUserExists(username, exists -> {
            runOnUiThread(() -> {
                if (exists) {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_user_exists), Toast.LENGTH_SHORT).show();
                    // Si no existe, lo creamos
                } else {
                    User newUser = new User(username, password);
                    // Damos un saldo inicial de bienvenida (opcional)
                    newUser.setBerries(50);

                    repository.insertUser(newUser);

                    Toast.makeText(LoginActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                    // Opcional: Podrías llamar a performLogin aquí automáticamente,
                    // pero es mejor obligar al usuario a loguearse para confirmar.
                }
            });
        });
    }
}

