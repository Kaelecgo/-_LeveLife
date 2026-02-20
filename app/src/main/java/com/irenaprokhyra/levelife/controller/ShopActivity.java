package com.irenaprokhyra.levelife.controller;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.view.FurnitureAdapter;

import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private int currentUserId;
    private User currentUser;
    private MainRepository repository;

    private TextView tvShopBalance;
    private RecyclerView rvFurniture;
    private FurnitureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, getString(R.string.error_session_lost), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = MainRepository.getInstance(getApplication());

        initViews();
        loadUserData();
        loadCatalog();
    }

    private void initViews() {
        tvShopBalance = findViewById(R.id.tvShopBalance);
        rvFurniture = findViewById(R.id.rvFurniture);

        // Usamos un GridLayout de 2 columnas para que parezca una tienda
        rvFurniture.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new FurnitureAdapter(furniture -> {
            // Fase 1: Solo mostramos un mensaje al hacer click
            // En la Fase 2, aquí restaremos las bayas y daremos el mueble.
            String msg = getString(R.string.toast_furniture_selected, furniture.getName(), furniture.getPrice());
            Toast.makeText(ShopActivity.this, msg, Toast.LENGTH_SHORT).show();
        });
        rvFurniture.setAdapter(adapter);
    }

    private void loadUserData() {
        repository.getUserById(currentUserId, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                runOnUiThread(() -> {
                    // Mostramos las bayas en la parte superior de la tienda
                    tvShopBalance.setText(getString(R.string.shop_balance_format, user.getBerries()));
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(ShopActivity.this, getString(R.string.error_load_balance), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadCatalog() {
        repository.getAllFurniture(new MainRepository.FurnitureListCallback() {
            @Override
            public void onSuccess(List<Furniture> furnitureList) {
                runOnUiThread(() -> adapter.setFurnitureList(furnitureList));

            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(ShopActivity.this, getString(R.string.error_load_catalog), Toast.LENGTH_SHORT).show());
            }
        });
    }
}