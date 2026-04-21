package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.util.DialogUtils;
import com.irenaprokhyra.levelife.view.FurnitureAdapter;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private int currentUserId;
    private MainViewModel viewModel;
    private User currentUser;
    private TextView tvShopBerriesBalance;
    private TextView tvShopEcoBalance;
    private RecyclerView rvFurniture;
    private FurnitureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

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

    private void initViews() {
        tvShopBerriesBalance = findViewById(R.id.tvShopBerriesBalance);
        tvShopEcoBalance = findViewById(R.id.tvShopEcoBalance);
        rvFurniture = findViewById(R.id.rvFurniture);
        rvFurniture.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new FurnitureAdapter(this::attemptPurchase);
        rvFurniture.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                this.currentUser = user;
                tvShopBerriesBalance.setText(getString(R.string.shop_balance_berries_format, user.getBerries()));
                tvShopEcoBalance.setText(getString(R.string.shop_balance_eco_format, user.getEcoCoins()));
                adapter.setBalances(user.getBerries(), user.getEcoCoins());
            }
        });

        viewModel.getShopCatalog().observe(this, furnitureList -> {
            if (furnitureList != null) {
                adapter.setFurnitureList(furnitureList);
            }
        });

        viewModel.getInventory().observe(this, inventory -> {
            if (inventory != null) {
                List<Integer> ownedIds = new ArrayList<>();
                for (Furniture f : inventory) {
                    ownedIds.add(f.getId());
                }
                adapter.setOwnedFurnitureIds(ownedIds);
            }
        });

        viewModel.getErrorMessages().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptPurchase(Furniture furniture) {
        if (currentUser == null) return;

        viewModel.purchaseFurniture(furniture, () -> {
            runOnUiThread(() -> {
                Toast.makeText(this, getString(R.string.success_buy_furniture, furniture.getName()), Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav == null) return;

        //bottomNav.setItemIconTintList(null);
        bottomNav.setSelectedItemId(R.id.nav_shop);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_shop) {
                return true;
            } else if (itemId == R.id.nav_inventory) {
                navigateTo(InventoryActivity.class);
                return true;
            } else if (itemId == R.id.nav_tasks) {
                navigateTo(TaskActivity.class);
                return true;
            } else if (itemId == R.id.nav_logout) {
                DialogUtils.showLogoutConfirmationDialog(this, this::performLogout);
                return false;
            }
            return false;
        });
    }

    private void navigateTo(Class<?> destinationClass) {
        Intent intent = new Intent(this, destinationClass);
        intent.putExtra("USER_ID", currentUserId);
        startActivity(intent);
        finish();
    }

    private void performLogout() {
        getSharedPreferences("LeveLifeSession", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
