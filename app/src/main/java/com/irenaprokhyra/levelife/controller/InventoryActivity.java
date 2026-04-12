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
import com.irenaprokhyra.levelife.model.PlacedFurnitureItem;
import com.irenaprokhyra.levelife.util.DialogUtils;
import com.irenaprokhyra.levelife.view.InventoryAdapter;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;
import java.util.HashMap;
import java.util.Map;

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
            Toast.makeText(this, "Sesión perdida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.init(currentUserId);

        initViews();
        setupObservers();
        setupNavigation();
    }

    private void initViews() {
        rvInventory = findViewById(R.id.rvInventory);
        rvInventory.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new InventoryAdapter(new InventoryAdapter.OnFurnitureInteractionListener() {
            @Override
            public void onPlaceClick(Furniture furniture) {
                // Ejecutamos la lógica de colocar/mover
                placeFurnitureDirectly(furniture);
            }

            @Override
            public void onDeleteClick(Furniture furniture) {
                // Ejecutamos la lógica de borrado
                confirmDeleteFurniture(furniture);
            }
        });

        rvInventory.setAdapter(adapter);
    }

    private void placeFurnitureDirectly(Furniture furniture) {
        // 1. Navegamos inmediatamente a la habitación para dar fluidez
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_ID", currentUserId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        // 2. Registramos la colocación en el ViewModel en segundo plano
        viewModel.placeFurniture(furniture, furniture.getType(), () -> {
            // Se guarda silenciosamente mientras el usuario ya está en la habitación
        });

        finish(); // Cerramos el inventario
    }

    private void confirmDeleteFurniture(Furniture furniture) {
        DialogUtils.showRemovePlacedFurnitureConfirmationDialog(this, furniture.getName(), () -> {
            String slotReal = null;
            if (viewModel.getPlacedFurniture().getValue() != null) {
                for (PlacedFurnitureItem item : viewModel.getPlacedFurniture().getValue()) {
                    if (item.getFurnitureId() == furniture.getId()) {
                        slotReal = item.getSlot();
                        break;
                    }
                }
            }

            if (slotReal != null) {
                viewModel.removePlacedFurniture(slotReal, () -> {
                    runOnUiThread(() -> Toast.makeText(this, "Mueble quitado de la habitación", Toast.LENGTH_SHORT).show());
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "El mueble no está colocado", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupObservers() {
        // Observador para actualizar qué muebles tienen el botón "Move" y la papelera
        viewModel.getPlacedFurniture().observe(this, placedItems -> {
            Map<Integer, String> slotsMap = new HashMap<>();
            if (placedItems != null) {
                for (PlacedFurnitureItem item : placedItems) {
                    slotsMap.put(item.getFurnitureId(), item.getSlot());
                }
            }
            adapter.setPlacedFurnitureSlots(slotsMap);
        });

        // Observador para la lista de muebles que posee el usuario
        viewModel.getInventory().observe(this, list -> {
            if (list != null) {
                adapter.setInventoryList(list);
            }
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav == null) return;

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
            } else if (itemId == R.id.nav_tasks) {
                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_logout) {
                // Diálogo de cerrar sesión
                DialogUtils.showLogoutConfirmationDialog(this, this::performLogout);
                return false;
            }
            return false;
        });
    }

    private void performLogout() {
        // Limpiamos la sesión
        getSharedPreferences("LeveLifeSession", MODE_PRIVATE).edit().clear().apply();

        // Volvemos al Login borrando el historial de actividades
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}