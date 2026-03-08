package com.irenaprokhyra.levelife.controller;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.User;


public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvLevel, tvBerries;
    private Button btnTasks, btnShop, btnInventory, btnLogout;

    private MainRepository repository;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recuperar ID
        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        // Protección de Navegación
        if (currentUserId == -1) {
            // Si por error llegamos aquí sin ID, volvemos al Login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        repository = MainRepository.getInstance(getApplication());

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // >_ IMPORTANTE | Recarga datos al volver de Tareas o Tienda _<
        // Así siempre vemos las bayas y XP actualizadas
        loadUserData();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvLevel = findViewById(R.id.tvLevel);
        tvBerries = findViewById(R.id.tvBerries);

        btnTasks = findViewById(R.id.btnTasks);
        btnShop = findViewById(R.id.btnShop);
        btnInventory = findViewById(R.id.btnInventory);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void updateUI(User user) {
        // >_ Usamos String Formatting para textos limpios _<
        tvWelcome.setText(getString(R.string.main_welcome_format, user.getName()));
        tvLevel.setText(getString(R.string.main_level_format, user.getLevel()));
        tvBerries.setText(getString(R.string.main_berries_format, user.getBerries()));
    }

    private void loadUserData() {
        // Usamos el Repositorio asíncrono
        repository.getUserById(currentUserId, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                // Volvemos al hilo principal para tocar la UI
                runOnUiThread(() -> updateUI(user));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, getString(R.string.error_load_user),
                                Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupListeners() {
        // >_ NAVEGACIÓN A TAREAS _<
        btnTasks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        // >_ NAVEGACIÓN A TIENDA _<
        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        /* // >_ NAVEGACIÓN A INVENTARIO (Hito 5 - Futuro) _<
        btnInventory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
            intent.putExtra("USER_ID", currentUserId); // >_ CORREGIDO: Faltaba pasar el ID _<
            startActivity(intent);
        });
        */

        // >_ LOGOUT _<
        btnLogout.setOnClickListener(v -> {
            // Delegamos la UI a DialogUtils. Si confirma, llamamos al metodo de borrado de sesión.
            com.irenaprokhyra.levelife.util.DialogUtils.showLogoutConfirmationDialog(this, this::performLogout);
        });
    }


    // Metodo para cerrar sesion
    private void performLogout() {
        // Borra sesion
        clearSessionPreferences();
        // Volvemos al Login y limpiamos la pila de actividades (para no poder volver atrás)
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // >_ BORRAR SESIÓN _<
    private void clearSessionPreferences() {
        getSharedPreferences("LeveLifeSession", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
