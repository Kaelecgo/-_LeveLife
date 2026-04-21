package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.irenaprokhyra.levelife.util.RoomPlacementRules;
import com.irenaprokhyra.levelife.view.InventoryAdapter;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {

    private int currentUserId;
    private MainViewModel viewModel;
    private RecyclerView rvInventory;
    private InventoryAdapter adapter;
    private View layoutEmptyState;
    private final Map<Integer, PlacedFurnitureItem> placedFurnitureByFurnitureId = new HashMap<>();
    private final Map<String, PlacedFurnitureItem> placedFurnitureBySlot = new HashMap<>();

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

    private void initViews() {
        rvInventory = findViewById(R.id.rvInventory);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        rvInventory.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new InventoryAdapter(new InventoryAdapter.OnFurnitureInteractionListener() {
            @Override
            public void onPlaceClick(Furniture furniture) {
                showSlotPicker(furniture);
            }

            @Override
            public void onRemoveClick(Furniture furniture) {
                handleRemoveRequest(furniture);
            }
        });
        rvInventory.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getInventory().observe(this, furnitureList -> {
            adapter.setInventoryList(furnitureList);

            if (furnitureList == null || furnitureList.isEmpty()) {
                rvInventory.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            } else {
                rvInventory.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            }
        });

        viewModel.getPlacedFurniture().observe(this, this::updatePlacedFurnitureState);

        viewModel.getErrorMessages().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSlotPicker(Furniture furniture) {
        List<String> allowedSlots = RoomPlacementRules.getAllowedSlots(furniture);
        DialogUtils.showFurnitureSlotPickerBottomSheet(
                this,
                furniture,
                allowedSlots,
                slot -> handlePlacementRequest(furniture, slot)
        );
    }

    private void handlePlacementRequest(Furniture furniture, String slot) {
        PlacedFurnitureItem sameFurniture = placedFurnitureByFurnitureId.get(furniture.getId());
        String slotLabel = getSlotLabel(slot);

        if (sameFurniture != null && slot.equals(
                RoomPlacementRules.normalizeStoredSlot(
                        sameFurniture.getSlot(),
                        sameFurniture.getType(),
                        sameFurniture.getImageRef()
                ))) {
            Toast.makeText(
                    this,
                    getString(R.string.inventory_slot_already_selected, slotLabel),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        PlacedFurnitureItem occupyingFurniture = placedFurnitureBySlot.get(slot);
        if (occupyingFurniture != null && occupyingFurniture.getFurnitureId() != furniture.getId()) {
            DialogUtils.showReplaceFurnitureConfirmationDialog(
                    this,
                    slotLabel,
                    occupyingFurniture.getName(),
                    furniture.getName(),
                    () -> placeFurnitureInRoom(furniture, slot)
            );
            return;
        }

        placeFurnitureInRoom(furniture, slot);
    }

    private void placeFurnitureInRoom(Furniture furniture, String slot) {
        boolean repositioned = placedFurnitureByFurnitureId.containsKey(furniture.getId());
        String slotLabel = getSlotLabel(slot);
        viewModel.placeFurniture(furniture, slot, () -> runOnUiThread(() -> {
            String message = repositioned
                    ? getString(R.string.inventory_item_repositioned, furniture.getName(), slotLabel)
                    : getString(R.string.inventory_item_placed, furniture.getName());
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }));
    }

    private void handleRemoveRequest(Furniture furniture) {
        PlacedFurnitureItem placedItem = placedFurnitureByFurnitureId.get(furniture.getId());
        if (placedItem == null) {
            Toast.makeText(this, getString(R.string.inventory_item_not_placed), Toast.LENGTH_SHORT).show();
            return;
        }

        DialogUtils.showRemovePlacedFurnitureConfirmationDialog(
                this,
                furniture.getName(),
                () -> removeFurnitureFromRoom(placedItem)
        );
    }

    private void removeFurnitureFromRoom(PlacedFurnitureItem placedItem) {
        viewModel.removePlacedFurniture(placedItem.getSlot(), () -> runOnUiThread(() -> Toast.makeText(
                this,
                getString(R.string.inventory_item_removed, placedItem.getName()),
                Toast.LENGTH_SHORT
        ).show()));
    }

    private void updatePlacedFurnitureState(List<PlacedFurnitureItem> placedFurnitureItems) {
        placedFurnitureByFurnitureId.clear();
        placedFurnitureBySlot.clear();

        Map<Integer, String> placedFurnitureSlots = new HashMap<>();
        if (placedFurnitureItems != null) {
            for (PlacedFurnitureItem item : placedFurnitureItems) {
                if (item == null) {
                    continue;
                }
                String visualSlot = RoomPlacementRules.normalizeStoredSlot(
                        item.getSlot(),
                        item.getType(),
                        item.getImageRef()
                );
                placedFurnitureByFurnitureId.put(item.getFurnitureId(), item);
                placedFurnitureBySlot.put(visualSlot, item);
                placedFurnitureSlots.put(item.getFurnitureId(), getSlotLabel(visualSlot));
            }
        }

        adapter.setPlacedFurnitureSlots(placedFurnitureSlots);
    }

    private String getSlotLabel(String slot) {
        int labelRes = RoomPlacementRules.getSlotLabelRes(slot);
        if (labelRes != 0) {
            return getString(labelRes);
        }
        return slot;
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav == null) {
            return;
        }

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
            } else if (itemId == R.id.nav_inventory) {
                return true;
            } else if (itemId == R.id.nav_tasks) {
                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_logout) {
                DialogUtils.showLogoutConfirmationDialog(this, this::performLogout);
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
