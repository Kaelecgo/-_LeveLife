package com.irenaprokhyra.levelife.controller;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.User;


public class MainActivity extends AppCompatActivity {

    private int currentUserId;
    private MainRepository repository;

    private TextView tvMainLevel, tvMainBerries, tvMainXpText;
    private ProgressBar pbMainXp;

    private ImageButton btnMainLogout;
    private BottomNavigationView bottomNavigationView;

    // >_ VARIABLES DE ESTADO PARA ANIMACIONES _<
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

        repository = MainRepository.getInstance(getApplication());
        initViews();
        setupNavigation();
        setupBackButtonBlock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDashboard();

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initViews() {
        tvMainLevel = findViewById(R.id.tvMainLevel);
        tvMainBerries = findViewById(R.id.tvMainBerries);
        tvMainXpText = findViewById(R.id.tvMainXpText);
        pbMainXp = findViewById(R.id.pbMainXp);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
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
                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_logout) {
                com.irenaprokhyra.levelife.util.DialogUtils.showLogoutConfirmationDialog(this, this::performLogout);
            }
                return false;
        });
    }

    private void navigateTo(Class<?> destinationClass) {
        Intent intent = new Intent(MainActivity.this, destinationClass);
        intent.putExtra("USER_ID", currentUserId);
        startActivity(intent);
    }

    private void loadUserDashboard() {
        repository.getUserById(currentUserId, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                if (user == null) return;
                runOnUiThread(() -> updateUI(user));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, getString(R.string.error_load_user), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateUI(User user) {
        try {
            int currentLevel = user.getLevel();
            int currentProgress = user.getProgressPercentage();

            pbMainXp.setMax(100);

            String xpText = getString(R.string.main_xp_format, user.getExperience(), user.getXpToNextLevel());
            tvMainXpText.setText(xpText);

            tvMainBerries.setText(getString(R.string.main_berries_format, user.getBerries()));

            pbMainXp.postDelayed(() -> {
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
            }, 400);

        } catch (Exception e) {
            Log.e("MainActivity", "Error actualizando UI: " + e.getMessage());
        }
    }

    // >_ SECUENCIA DE ANIMACIÓN PROFESIONAL _<
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