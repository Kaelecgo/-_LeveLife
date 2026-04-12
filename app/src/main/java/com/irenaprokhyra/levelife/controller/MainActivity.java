package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.PlacedFurniture;
import com.irenaprokhyra.levelife.model.PlacedFurnitureItem;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.util.DialogUtils;
import com.irenaprokhyra.levelife.util.FurnitureDrawableResolver;
import com.irenaprokhyra.levelife.util.SessionManager;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int currentUserId;
    private MainViewModel viewModel;
    private MainRepository repository;

    private TextView tvMainSectionLabel, tvRoomSubtitle, tvMainLevel, tvMainBerries, tvMainEcoCoins, tvMainXpText;
    private ProgressBar pbMainXp;
    private BottomNavigationView bottomNavigationView;

    private View layoutRoomEmptyState;
    private ImageView ivPlacedWall, ivPlacedFloor, ivPlacedDesk, ivPlacedDecor;
    private final Map<String, PlacedFurnitureItem> placedFurnitureBySlot = new HashMap<>();

    // Variables de control
    private int xDelta, yDelta;
    private ScaleGestureDetector scaleGestureDetector;
    private View viewActiva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = MainRepository.getInstance(getApplication());
        currentUserId = SessionManager.getSavedUserId(this);

        if (currentUserId == -1) { redirectToLogin(); return; }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.init(currentUserId);

        initViews();
        setupObservers();
        setupNavigation();
        setupBackButtonBlock();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    private void initViews() {
        tvMainSectionLabel = findViewById(R.id.tvMainSectionLabel);
        tvRoomSubtitle = findViewById(R.id.tvRoomSubtitle);
        tvMainLevel = findViewById(R.id.tvMainLevel);
        tvMainBerries = findViewById(R.id.tvMainBerries);
        tvMainEcoCoins = findViewById(R.id.tvMainEcoCoins);
        tvMainXpText = findViewById(R.id.tvMainXpText);
        pbMainXp = findViewById(R.id.pbMainXp);
        layoutRoomEmptyState = findViewById(R.id.layoutRoomEmptyState);

        ivPlacedWall = findViewById(R.id.ivPlacedWall);
        ivPlacedFloor = findViewById(R.id.ivPlacedFloor);
        ivPlacedDesk = findViewById(R.id.ivPlacedDesk);
        ivPlacedDecor = findViewById(R.id.ivPlacedDecor);

        // Configuramos el comportamiento de cada mueble
        configurarMueble(ivPlacedWall, PlacedFurniture.SLOT_WALL);
        configurarMueble(ivPlacedFloor, PlacedFurniture.SLOT_FLOOR);
        configurarMueble(ivPlacedDesk, PlacedFurniture.SLOT_DESK);
        configurarMueble(ivPlacedDecor, PlacedFurniture.SLOT_DECOR);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
    }

    private void configurarMueble(ImageView iv, String slot) {
        // Solo dejamos el listener de movimiento y escala
        iv.setOnTouchListener(muebleTouchListener);

        // BORRA O COMENTA ESTA PARTE:
    /*
    iv.setOnLongClickListener(v -> {
        showPlacedFurnitureActions(slot);
        return true;
    });
    */

        // Si quieres que el LongClick no haga nada absoluto:
        iv.setOnLongClickListener(null);
    }

    private final View.OnTouchListener muebleTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            viewActiva = view;
            scaleGestureDetector.onTouchEvent(event);

            final int x = (int) event.getRawX();
            final int y = (int) event.getRawY();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    AbsoluteLayout.LayoutParams lParams = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
                    xDelta = x - lParams.x;
                    yDelta = y - lParams.y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!scaleGestureDetector.isInProgress()) {
                        AbsoluteLayout.LayoutParams layoutParams = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.x = x - xDelta;
                        layoutParams.y = y - yDelta;
                        view.setLayoutParams(layoutParams);
                    }
                    break;
            }
            return false; // Importante para que el LongClick funcione
        }
    };

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (viewActiva != null) {
                float scale = viewActiva.getScaleX() * detector.getScaleFactor();
                // Ponemos límites para que no desaparezca ni ocupe toda la pantalla
                scale = Math.max(0.2f, Math.min(scale, 3.0f));
                viewActiva.setScaleX(scale);
                viewActiva.setScaleY(scale);
            }
            return true;
        }
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
            placedFurnitureBySlot.put(item.getSlot(), item);
            ImageView target = getTargetViewForSlot(item.getSlot());
            if (target != null) {
                target.setImageResource(FurnitureDrawableResolver.resolveDrawableResId(this, item.getImageRef()));
                target.setVisibility(View.VISIBLE);

                // Si la base de datos ya tiene coordenadas, aquí deberías aplicarlas:
                // AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) target.getLayoutParams();
                // params.x = item.getPosX(); ...
            }
        }
    }

    // --- MÉTODOS DE NAVEGACIÓN Y UI (Sin cambios) ---

    private void showPlacedFurnitureActions(String slot) {
        PlacedFurnitureItem item = placedFurnitureBySlot.get(slot);
        if (item == null) return;
        DialogUtils.showPlacedFurnitureManagementDialog(this, item.getName(),
                () -> navigateTo(InventoryActivity.class), // Move: Te lleva al inventario
                () -> confirmRemovePlacedFurniture(item)   // Remove: Lo quita
        );
    }

    private void confirmRemovePlacedFurniture(PlacedFurnitureItem item) {
        DialogUtils.showRemovePlacedFurnitureConfirmationDialog(this, item.getName(), () -> {
            viewModel.removePlacedFurniture(item.getSlot(), () -> {
                runOnUiThread(() -> Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show());
            });
        });
    }

    private ImageView getTargetViewForSlot(String slot) {
        if (PlacedFurniture.SLOT_WALL.equals(slot)) return ivPlacedWall;
        if (PlacedFurniture.SLOT_FLOOR.equals(slot)) return ivPlacedFloor;
        if (PlacedFurniture.SLOT_DESK.equals(slot)) return ivPlacedDesk;
        if (PlacedFurniture.SLOT_DECOR.equals(slot)) return ivPlacedDecor;
        return null;
    }

    private void clearPlacedFurnitureViews() {
        ivPlacedWall.setVisibility(View.GONE);
        ivPlacedFloor.setVisibility(View.GONE);
        ivPlacedDesk.setVisibility(View.GONE);
        ivPlacedDecor.setVisibility(View.GONE);
    }

    private void setupObservers() {
        viewModel.getUser().observe(this, user -> { if (user != null) updateUI(user); });
        viewModel.getPlacedFurniture().observe(this, this::renderPlacedFurniture);
    }

    private void updateUI(User user) {
        tvMainSectionLabel.setText(getString(R.string.main_welcome_format, user.getName()));
        tvMainLevel.setText(getString(R.string.main_level_format, user.getLevel()));
        tvMainBerries.setText(String.valueOf(user.getBerries()));
        tvMainEcoCoins.setText(String.valueOf(user.getEcoCoins()));
        pbMainXp.setProgress(user.getProgressPercentage());
    }

    private void setupNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_shop) navigateTo(ShopActivity.class);
            if (id == R.id.nav_inventory) navigateTo(InventoryActivity.class);
            if (id == R.id.nav_tasks) navigateTo(TaskActivity.class);
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
            @Override public void handleOnBackPressed() { moveTaskToBack(true); }
        });
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}