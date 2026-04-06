package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.model.PlacedFurniture;
import com.irenaprokhyra.levelife.view.InventoryAdapter;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;


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
            Toast.makeText(this, getString(R.string.common_error_session_lost), Toast.LENGTH_SHORT).show();
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

        adapter = new InventoryAdapter(this::showSlotPicker);
        rvInventory.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getInventory().observe(this, furnitureList -> {
            if (furnitureList == null || furnitureList.isEmpty()) {
                Toast.makeText(this, getString(R.string.empty_inventory), Toast.LENGTH_SHORT).show();
            }
            adapter.setInventoryList(furnitureList);
        });

        viewModel.getErrorMessages().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSlotPicker(Furniture furniture) {
        if (furniture == null) return;

        CharSequence[] slotLabels = new CharSequence[] {
                "Suelo",
                "Pared",
                "Escritorio",
                "Decoración"
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle("Elegir zona")
                .setItems(slotLabels, (dialog, which) -> {
                    String slot = mapSlotFromIndex(which);
                    placeFurnitureInRoom(furniture, slot);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private String mapSlotFromIndex(int which) {
        switch (which) {
            case 0:
                return PlacedFurniture.SLOT_FLOOR;
            case 1:
                return PlacedFurniture.SLOT_WALL;
            case 2:
                return PlacedFurniture.SLOT_DESK;
            case 3:
            default:
                return PlacedFurniture.SLOT_DECOR;
        }
    }

    private void placeFurnitureInRoom(Furniture furniture, String slot) {
        viewModel.placeFurniture(furniture, slot, () -> {
            runOnUiThread(() -> {
                String message = getString(R.string.inventory_item_placed, furniture.getName());
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav= findViewById(R.id.bottomNavigationView);
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
            } else if (itemId == R.id.nav_inventory) { return true; }
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
