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

        // >_ Pasamos el metodo modular como referencia _<
        adapter = new FurnitureAdapter(this::attemptPurchase);
        rvFurniture.setAdapter(adapter);
    }

    private void attemptPurchase(Furniture furniture) {
        // Evitamos crasheo si el usuario clica antes de que cargue la BD
        if (currentUser == null) return;

        // >_ MEJORA: Delegamos la lógica matemática al Modelo _<
        // spendBerries devuelve true si hay fondos suficientes y resta el saldo automáticamente
        if (currentUser.spendBerries(furniture.getPrice())) {

            // Guardamos el nuevo saldo en la BD local
            repository.updateUser(currentUser);

            // Guardamos en el inventario (Tabla Intermedia N:M)
            repository.buyFurniture(currentUser.getId(), furniture.getId());

            // Actualizamos la UI
            tvShopBalance.setText(getString(R.string.shop_balance_format, currentUser.getBerries()));

            // >_ MEJORA UX | Avisamos al adaptador de que tenemos menos dinero _<
            // Esto re-evaluará todos los muebles y bloqueará los que ya no podamos pagar
            adapter.setCurrentBalance(currentUser.getBerries());

            // Feedback de exito
            Toast.makeText(this, getString(R.string.success_buy_furniture, furniture.getName()), Toast.LENGTH_SHORT).show();

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
}