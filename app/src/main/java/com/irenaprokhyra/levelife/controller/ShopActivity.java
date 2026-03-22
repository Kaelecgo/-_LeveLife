package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
        setupNavigation();
        loadUserData();
        loadCatalog();
        loadOwnedFurniture();
    }

    private void initViews() {
        tvShopBalance = findViewById(R.id.tvShopBalance);
        rvFurniture = findViewById(R.id.rvFurniture);

        // Usamos un GridLayout de 2 columnas para que parezca una tienda
        rvFurniture.setLayoutManager(new GridLayoutManager(this, 2));

        // >_ Pasamos el metodo modular como referencia _<
        adapter = new FurnitureAdapter(this::attemptPurchase);
        rvFurniture.setAdapter(adapter);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav= findViewById(R.id.bottomNavigationView);
        if (bottomNav == null) return;

        bottomNav.setItemIconTintList(null);

        bottomNav.setSelectedItemId(R.id.nav_shop);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_shop) { return true; }
            else if (itemId == R.id.nav_inventory) {
                Intent intent = new Intent(this, InventoryActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_tasks) {
                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                finish(); // Cerramos la actual
                return true;
            } else if (itemId == R.id.nav_logout) {
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

    private void attemptPurchase(Furniture furniture) {
        // Evitamos crasheo si el usuario clica antes de que cargue la BD
        if (currentUser == null) return;

        // >_ MEJORA: Delegamos la lógica matemática al Modelo _<
        // spendBerries devuelve true si hay fondos suficientes y resta el saldo automáticamente
        if (currentUser.spendBerries(furniture.getPrice())) {

            // >_ PREPARACIÓN: dejamos estructurado el metodo unificado _<
            repository.buyFurnitureTransaction(currentUser, furniture.getId(), () -> {

                // Volvemos al hilo principal para tocar la pantalla
                runOnUiThread(() -> {
                    // Actualizamos el texto del saldo superior
                    tvShopBalance.setText(getString(R.string.shop_balance_format, currentUser.getBerries()));

                    // Avisamos al adaptador de que tenemos menos dinero
                    adapter.setCurrentBalance(currentUser.getBerries());

                    // Marcamos este mueble específico como comprado al instante
                    adapter.markAsOwned(furniture.getId());

                    // Feedback de éxito 100% seguro
                    Toast.makeText(this, getString(R.string.success_buy_furniture, furniture.getName()), Toast.LENGTH_SHORT).show();
                });
            });

        } else {
            // Feedback de rechazo por falta de fondos
            Toast.makeText(this, getString(R.string.error_not_enough_berries), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        repository.getUserById(currentUserId, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                runOnUiThread(() -> {
                    // Mostramos las bayas en la parte superior de la tienda
                    tvShopBalance.setText(getString(R.string.shop_balance_format, user.getBerries()));

                    // >_ Al cargar el perfil, le pasamos el saldo inicial al adaptador _<
                    adapter.setCurrentBalance(user.getBerries());
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

    // >_ CARGA DE MEMORIA DE LA TIENDA _<
    private void loadOwnedFurniture() {
        // Usamos el metodo que programamos en el rol A1
        repository.getOwnedFurnitureIds(currentUserId, null, new MainRepository.OwnedIdsCallback() {
            @Override
            public void onSuccess(List<Integer> ownedIds) {
                // Volvemos al hilo principal para tocar la interfaz
                runOnUiThread(() -> {
                    // Inyectamos la lista de IDs comprados en el adaptador
                    adapter.setOwnedFurnitureIds(ownedIds);
                });
            }

            @Override
            public void onError(String message) {
                // Si hay error, simplemente la tienda no marcará nada como comprado
                android.util.Log.e("ShopActivity", "Aviso Tienda: " + message);
            }
        });
    }
}