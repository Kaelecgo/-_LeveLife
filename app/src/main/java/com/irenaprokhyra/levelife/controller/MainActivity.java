package com.irenaprokhyra.levelife.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.PlacedFurniture;
import com.irenaprokhyra.levelife.model.PlacedFurnitureItem;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.util.DialogUtils;
import com.irenaprokhyra.levelife.util.FurnitureDrawableResolver;
import com.irenaprokhyra.levelife.util.RoomPlacementRules;
import com.irenaprokhyra.levelife.util.SessionManager;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int currentUserId;
    private MainViewModel viewModel;

    private TextView tvMainSectionLabel;
    private TextView tvMainLevel;
    private TextView tvMainBerries;
    private TextView tvMainEcoCoins;
    private TextView tvMainXpText;
    private ProgressBar pbMainXp;
    private BottomNavigationView bottomNavigationView;

    private View layoutRoomEmptyState;
    private ImageView ivPlacedWallShowcase;
    private ImageView ivPlacedBedNook;
    private ImageView ivPlacedRugCenter;
    private ImageView ivPlacedFloorLeft;
    private ImageView ivPlacedFloorRight;
    private ImageView ivPlacedSurfaceLeft;
    private ImageView ivPlacedSurfaceRight;
    private final Map<String, PlacedFurnitureItem> placedFurnitureBySlot = new HashMap<>();
    private int lastKnownLevel = -1;
    private int lastKnownExperience = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUserId = SessionManager.getSavedUserId(this);
        if (currentUserId == -1) {
            redirectToLogin();
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
        layoutRoomEmptyState = findViewById(R.id.layoutRoomEmptyState);

        ivPlacedWallShowcase = findViewById(R.id.ivPlacedWallShowcase);
        ivPlacedBedNook = findViewById(R.id.ivPlacedBedNook);
        ivPlacedRugCenter = findViewById(R.id.ivPlacedRugCenter);
        ivPlacedFloorLeft = findViewById(R.id.ivPlacedFloorLeft);
        ivPlacedFloorRight = findViewById(R.id.ivPlacedFloorRight);
        ivPlacedSurfaceLeft = findViewById(R.id.ivPlacedSurfaceLeft);
        ivPlacedSurfaceRight = findViewById(R.id.ivPlacedSurfaceRight);

        configureFurnitureView(ivPlacedWallShowcase, PlacedFurniture.SLOT_WALL_SHOWCASE);
        configureFurnitureView(ivPlacedBedNook, PlacedFurniture.SLOT_BED_NOOK);
        configureFurnitureView(ivPlacedRugCenter, PlacedFurniture.SLOT_RUG_CENTER);
        configureFurnitureView(ivPlacedFloorLeft, PlacedFurniture.SLOT_FLOOR_LEFT);
        configureFurnitureView(ivPlacedFloorRight, PlacedFurniture.SLOT_FLOOR_RIGHT);
        configureFurnitureView(ivPlacedSurfaceLeft, PlacedFurniture.SLOT_SURFACE_LEFT);
        configureFurnitureView(ivPlacedSurfaceRight, PlacedFurniture.SLOT_SURFACE_RIGHT);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void configureFurnitureView(ImageView imageView, String slot) {
        imageView.setOnClickListener(v -> showPlacedFurnitureActions(slot));
        imageView.setOnLongClickListener(v -> {
            showPlacedFurnitureActions(slot);
            return true;
        });
    }

    private void renderPlacedFurniture(List<PlacedFurnitureItem> items) {
        placedFurnitureBySlot.clear();
        clearPlacedFurnitureViews();

        if (items == null || items.isEmpty()) {
            layoutRoomEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        layoutRoomEmptyState.setVisibility(View.GONE);
        for (PlacedFurnitureItem item : items) {
            String visualSlot = RoomPlacementRules.normalizeStoredSlot(
                    item.getSlot(),
                    item.getType(),
                    item.getImageRef()
            );
            placedFurnitureBySlot.put(visualSlot, item);

            ImageView target = getTargetViewForSlot(visualSlot);
            if (target == null) {
                continue;
            }

            target.setImageResource(FurnitureDrawableResolver.resolveDrawableResId(this, item.getImageRef()));
            target.setContentDescription(item.getName());
            target.setVisibility(View.VISIBLE);
        }
    }

    private void showPlacedFurnitureActions(String slot) {
        PlacedFurnitureItem item = placedFurnitureBySlot.get(slot);
        if (item == null) {
            return;
        }

        DialogUtils.showPlacedFurnitureManagementDialog(
                this,
                item.getName(),
                () -> navigateTo(InventoryActivity.class),
                () -> confirmRemovePlacedFurniture(item)
        );
    }

    private void confirmRemovePlacedFurniture(PlacedFurnitureItem item) {
        DialogUtils.showRemovePlacedFurnitureConfirmationDialog(this, item.getName(), () ->
                viewModel.removePlacedFurniture(item.getSlot(), () -> runOnUiThread(() ->
                        Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                )));
    }

    private ImageView getTargetViewForSlot(String slot) {
        switch (slot) {
            case PlacedFurniture.SLOT_WALL_SHOWCASE:
                return ivPlacedWallShowcase;
            case PlacedFurniture.SLOT_BED_NOOK:
                return ivPlacedBedNook;
            case PlacedFurniture.SLOT_RUG_CENTER:
                return ivPlacedRugCenter;
            case PlacedFurniture.SLOT_FLOOR_LEFT:
                return ivPlacedFloorLeft;
            case PlacedFurniture.SLOT_FLOOR_RIGHT:
                return ivPlacedFloorRight;
            case PlacedFurniture.SLOT_SURFACE_LEFT:
                return ivPlacedSurfaceLeft;
            case PlacedFurniture.SLOT_SURFACE_RIGHT:
                return ivPlacedSurfaceRight;
            default:
                return null;
        }
    }

    private void clearPlacedFurnitureViews() {
        ivPlacedWallShowcase.setVisibility(View.GONE);
        ivPlacedBedNook.setVisibility(View.GONE);
        ivPlacedRugCenter.setVisibility(View.GONE);
        ivPlacedFloorLeft.setVisibility(View.GONE);
        ivPlacedFloorRight.setVisibility(View.GONE);
        ivPlacedSurfaceLeft.setVisibility(View.GONE);
        ivPlacedSurfaceRight.setVisibility(View.GONE);
    }

    private void setupObservers() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                updateUI(user);
            }
        });
        viewModel.getPlacedFurniture().observe(this, this::renderPlacedFurniture);
    }

    private void updateUI(User user) {
        int xpToNextLevel = Math.max(1, user.getXpToNextLevel());
        int currentExperience = Math.max(0, Math.min(user.getExperience(), xpToNextLevel));

        tvMainSectionLabel.setText(getString(R.string.main_welcome_format, user.getName()));
        tvMainLevel.setText(getString(R.string.main_level_format, user.getLevel()));
        tvMainBerries.setText(String.valueOf(user.getBerries()));
        tvMainEcoCoins.setText(String.valueOf(user.getEcoCoins()));

        pbMainXp.post(() -> renderExperienceProgress(user.getLevel(), currentExperience, xpToNextLevel));
    }

    private void renderExperienceProgress(int level, int currentExperience, int xpToNextLevel) {
        if (lastKnownLevel == -1) {
            tvMainLevel.setText(getString(R.string.main_level_format, level));
            updateProgressVisuals(currentExperience, xpToNextLevel);
            lastKnownLevel = level;
            lastKnownExperience = currentExperience;
            return;
        }

        if (level > lastKnownLevel) {
            animateLevelUpProgress(level, currentExperience, xpToNextLevel);
            return;
        }

        tvMainLevel.setText(getString(R.string.main_level_format, level));
        animateProgress(lastKnownExperience, currentExperience, xpToNextLevel, () -> {
            lastKnownLevel = level;
            lastKnownExperience = currentExperience;
        });
    }

    private void animateLevelUpProgress(int targetLevel, int targetExperience, int targetXpToNextLevel) {
        int previousMax = Math.max(1, lastKnownLevel * 100);
        int startProgress = Math.max(0, Math.min(lastKnownExperience, previousMax));

        pbMainXp.setMax(previousMax);
        updateProgressText(startProgress, previousMax);

        ObjectAnimator animateToMax = ObjectAnimator.ofInt(pbMainXp, "progress", startProgress, previousMax);
        animateToMax.setDuration(550L);
        animateToMax.addUpdateListener(animation ->
                updateProgressText((int) animation.getAnimatedValue(), previousMax)
        );
        animateToMax.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvMainLevel.setText(getString(R.string.main_level_format, targetLevel));
                pbMainXp.setMax(targetXpToNextLevel);
                pbMainXp.setProgress(0);
                updateProgressText(0, targetXpToNextLevel);

                animateProgress(0, targetExperience, targetXpToNextLevel, () -> {
                    lastKnownLevel = targetLevel;
                    lastKnownExperience = targetExperience;
                });
            }
        });
        animateToMax.start();
    }

    private void animateProgress(int from, int to, int xpToNextLevel, Runnable onEnd) {
        int safeFrom = Math.max(0, Math.min(from, xpToNextLevel));
        int safeTo = Math.max(0, Math.min(to, xpToNextLevel));

        pbMainXp.setMax(xpToNextLevel);
        pbMainXp.setProgress(safeFrom);
        updateProgressText(safeFrom, xpToNextLevel);

        if (safeFrom == safeTo) {
            if (onEnd != null) {
                onEnd.run();
            }
            return;
        }

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(pbMainXp, "progress", safeFrom, safeTo);
        progressAnimator.setDuration(600L);
        progressAnimator.addUpdateListener(animation ->
                updateProgressText((int) animation.getAnimatedValue(), xpToNextLevel)
        );
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onEnd != null) {
                    onEnd.run();
                }
            }
        });
        progressAnimator.start();
    }

    private void updateProgressVisuals(int currentExperience, int xpToNextLevel) {
        pbMainXp.setMax(xpToNextLevel);
        pbMainXp.setProgress(currentExperience);
        updateProgressText(currentExperience, xpToNextLevel);
    }

    private void updateProgressText(int currentExperience, int xpToNextLevel) {
        tvMainXpText.setText(getString(R.string.main_xp_format, currentExperience, xpToNextLevel));
    }

    private void setupNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            }
            if (id == R.id.nav_shop) {
                navigateTo(ShopActivity.class);
            }
            if (id == R.id.nav_inventory) {
                navigateTo(InventoryActivity.class);
            }
            if (id == R.id.nav_tasks) {
                navigateTo(TaskActivity.class);
            }
            if (id == R.id.nav_logout) {
                DialogUtils.showLogoutConfirmationDialog(this, this::performLogout);
                return false;
            }
            return true;
        });
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("USER_ID", currentUserId);
        startActivity(intent);
    }

    private void setupBackButtonBlock() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void performLogout() {
        SessionManager.clearSession(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
