package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.view.TaskAdapter;

import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private int currentUserId;
    private MainRepository repository;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TextView tvEmptyState;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabAddTask;

    // >_ Variable para guardar al usuario actual en memoria _<
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, getString(R.string.error_session_lost), Toast.LENGTH_SHORT).show();
            finish(); // Volver atrás si no hay usuario
            return;
        }

        repository = MainRepository.getInstance(getApplication());

        initViews();
        loadUserProfile();
        setupNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvTasks);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(task -> {
            if (currentUser == null) return;

            task.setCompleted(true);

            boolean leveledUp = currentUser.addExperience(task.getRewardXP());
            currentUser.addBerries(task.getRewardBerries());

            showRewardToast(task.getRewardXP(), task.getRewardBerries());

            if (leveledUp) {
                showLevelUpDialog();
            }

            repository.updateUser(currentUser);
            repository.updateTask(task, this::loadTasks);
        });
        recyclerView.setAdapter(adapter);

        androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback simpleCallback =
                new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT | androidx.recyclerview.widget.ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getBindingAdapterPosition();
                        Task taskToDelete = adapter.getTaskAt(position);
                        repository.deleteTask(taskToDelete, TaskActivity.this::loadTasks);
                    }
                };
        new androidx.recyclerview.widget.ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);


        fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            com.irenaprokhyra.levelife.util.DialogUtils.showCreateTaskDialog(this, taskTitle -> {
                Task newTask = new Task(currentUserId, taskTitle, "", "General", 10, 10);
                repository.insertTask(newTask, this ::loadTasks);
            });
        });
    }


    private void loadUserProfile() {
        repository.getUserById(currentUserId, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                loadTasks();
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(TaskActivity.this, getString(R.string.error_load_user), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav= findViewById(R.id.bottomNavigationView);
        if (bottomNav == null) return;

        bottomNav.setItemIconTintList(null);

        bottomNav.setSelectedItemId(R.id.nav_tasks);

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
            }
            else if (itemId == R.id.nav_inventory) {
                Intent intent = new Intent(this, InventoryActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_tasks) { return true; }
            else if (itemId == R.id.nav_logout) {
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

    private void loadTasks() {
        // Pedimos al repositorio las tareas de ESTE usuario
        repository.getTaskForUser(currentUserId, new MainRepository.TaskListCallback() {

            @Override
            public void onSuccess(List<Task> tasks) {
                runOnUiThread(() -> {
                    // >_ METODO DE VERIFICACIÓN DE TAREAS _<
                    // Si no hay tareas, ocultamos lista, mostramos mensaje
                    if (tasks.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        // Si hay tareas, Mostramos lista, ocultamos mensaje
                        recyclerView.setVisibility(View.VISIBLE);
                        tvEmptyState.setVisibility(View.GONE);
                        adapter.setTasks(tasks);
                    }
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                    Toast.makeText(TaskActivity.this, getString(R.string.error_load_tasks), Toast.LENGTH_SHORT).show());
            }
        });
    }

    // >_ FEEDBACK VISUAL _<
    private void showRewardToast(int xp, int berries) {
        String msg = getString(R.string.reward_claimed, xp, berries);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // >_ CAMBIO: Renombrado de Toast a Dialog por precisión semántica _<
    private void showLevelUpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_levelup_title)
                .setMessage(getString(R.string.dialog_levelup_message, currentUser.getLevel()))
                .setPositiveButton(R.string.dialog_levelup_button, null)
                .show();
    }
}