package com.irenaprokhyra.levelife.controller;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.User;


public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvLevel, tvBerries;
    private Button btnTasks, btnShop, btnInventory;

    private MainRepository repository;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        if (currentUserId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        repository = MainRepository.getInstance(getApplication());

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvLevel = findViewById(R.id.tvLevel);
        tvBerries = findViewById(R.id.tvBerries);
        btnTasks = findViewById(R.id.btnTasks);
        btnShop = findViewById(R.id.btnShop);
        btnInventory = findViewById(R.id.btnInventory);
    }

    private void updateUI(User user) {
        tvWelcome.setText(getString(R.string.main_welcome) + " " + user.getName());
        tvLevel.setText(getString(R.string.label_level) + " " + user.getLevel());
        tvBerries.setText(getString(R.string.label_berries) + " " + user.getBerries());
    }

    private void loadUserData() {
        repository.getUserById(currentUserId, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
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
        btnTasks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

//        btnInventory.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
//            startActivity(intent);
//        }
    }

}