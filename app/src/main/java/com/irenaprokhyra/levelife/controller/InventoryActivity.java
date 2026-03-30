package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.view.InventoryAdapter;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private int currentUserId;
    private MainViewModel viewModel;
    private RecyclerView rvInventory;
    private InventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, getString(R.string.error_session_lost), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.init(currentUserId);

        initViews();
        setupNavigation();
        setupObservers();
    }

    private void initViews () {
        rvInventory = findViewById(R.id.rvInventory);
        rvInventory.setLayoutManager(new GridLayoutManager(this, 2));

        // >_ PASAMOS EL METODO COMO REFERENCIA PARA LA LÓGICA DE A3 _<
        adapter = new InventoryAdapter(this::placeFurnitureInRoom);
        rvInventory.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getInventory().observe(this, furnitureList -> {
            if (furnitureList == null || furnitureList.isEmpty()) {
                Toast.makeText(this, getString(R.string.empty_inventory), Toast.LENGTH_SHORT).show();
            }
            adapter.setInventoryList(furnitureList);
        });
    }

    // >_ LA LÓGICA (Colocar el mueble) _<
    private void placeFurnitureInRoom(Furniture furniture) {
        // En el próximo hito, aquí guardaremos el ID del mueble activo en SharedPreferences
        // o en la BD para que el mapa sepa qué PNG dibujar

        // Por ahora, damos feedback de éxito usando el string parametrizado
        String successMsg = getString(R.string.success_placed_furniture, furniture.getName());
        Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();

        // Opcional: Cerrar el inventario para simular que volvemos a la habitación
        // finish();

    }



    private void setupNavigation() {
        BottomNavigationView bottomNav= findViewById(R.id.bottomNavigationView);
        if (bottomNav == null) return;

        //bottomNav.setItemIconTintList(null);
        bottomNav.setSelectedItemId(R.id.nav_inventory);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_shop) {
                Intent intent = new Intent(this, ShopActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_inventory) { return true; }
            else if (itemId == R.id.nav_tasks) {
                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_logout) {
                com.irenaprokhyra.levelife.util.DialogUtils.showLogoutConfirmationDialog(this, this::performLogout);
                return false;
            }
            return false;
        });
    }

    private void performLogout() {
        getSharedPreferences("LeveLifeSession", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
