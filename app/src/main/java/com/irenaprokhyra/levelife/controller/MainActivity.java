package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.util.DialogUtils;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;


public class MainActivity extends AppCompatActivity {

    private int currentUserId;
    private MainViewModel viewModel;

    private TextView tvMainSectionLabel, tvMainLevel, tvMainBerries, tvMainEcoCoins, tvMainXpText;
    private ProgressBar pbMainXp;
    private BottomNavigationView bottomNavigationView;

    private int lastKnownLevel = -1;
    private int lastKnownProgress = -1;

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

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.init(currentUserId);

        initViews();
        setupObservers();
        setupNavigation();
        setupBackButtonBlock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initViews() {
        tvMainSectionLabel = findViewById(R.id.tvMainSectionLabel);
        tvMainLevel = findViewById(R.id.tvMainLevel);
        tvMainBerries = findViewById(R.id.tvMainBerries);
        tvMainEcoCoins = findViewById(R.id.tvMainEcoCoins);
        tvMainXpText = findViewById(R.id.tvMainXpText);
        pbMainXp = findViewById(R.id.pbMainXp);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
    }

    private void setupObservers() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                updateUI(user);
            }
        });
    }

    private void setupBackButtonBlock() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });
    }

    private void setupNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_shop) {
                navigateTo(ShopActivity.class);
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
    }

    private void updateUI(User user) {
        try {
            int currentLevel = user.getLevel();
            int currentProgress = user.getProgressPercentage();

            pbMainXp.setMax(100);

            // Actualizar el saludo con el nombre del usuario
            if (tvMainSectionLabel != null) {
                tvMainSectionLabel.setText(getString(R.string.main_welcome_format, user.getName()));
            }

            // Sincronización con strings_gamification.xml
            String xpText = getString(R.string.main_xp_format, user.getExperience(), user.getXpToNextLevel());
            tvMainXpText.setText(xpText);

            tvMainBerries.setText(getString(R.string.main_berries_short, user.getBerries()));
            tvMainEcoCoins.setText(getString(R.string.main_eco_short, user.getEcoCoins()));

            if (lastKnownLevel == -1) {
                tvMainLevel.setText(getString(R.string.main_level_format, currentLevel));
                pbMainXp.setProgress(currentProgress);
            } else if (currentLevel > lastKnownLevel) {
                animateLevelUp(currentLevel, currentProgress);
            } else if (currentProgress != lastKnownProgress) {
                tvMainLevel.setText(getString(R.string.main_level_format, currentLevel));
                android.animation.ObjectAnimator animNormal = android.animation.ObjectAnimator.ofInt(
                        pbMainXp, "progress", pbMainXp.getProgress(), currentProgress);
                animNormal.setDuration(1500);
                animNormal.start();
            }

            lastKnownLevel = currentLevel;
            lastKnownProgress = currentProgress;

        } catch (Exception e) {
            Log.e("MainActivity", "Error actualizando UI: " + e.getMessage());
        }
    }

    private void animateLevelUp(int targetLevel, int targetProgress) {
        android.animation.ObjectAnimator animateTo100 = android.animation.ObjectAnimator.ofInt(pbMainXp, "progress", pbMainXp.getProgress(), 100);
        animateTo100.setDuration(600);
        animateTo100.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                Toast.makeText(MainActivity.this, getString(R.string.dialog_levelup_message, targetLevel),
                        Toast.LENGTH_SHORT).show();

                tvMainLevel.setText(getString(R.string.main_level_format, targetLevel));
                pbMainXp.setProgress(0);

                android.animation.ObjectAnimator animateToRealProgress = android.animation.ObjectAnimator.ofInt(pbMainXp, "progress", 0, targetProgress);
                animateToRealProgress.setDuration(500);
                animateToRealProgress.start();
            }
        });
        animateTo100.start();
    }

    private void performLogout() {
        clearSessionPreferences();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearSessionPreferences() {
        getSharedPreferences("LeveLifeSession", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
