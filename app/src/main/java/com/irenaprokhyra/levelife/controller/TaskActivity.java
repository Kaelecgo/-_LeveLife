package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.util.DialogUtils;
import com.irenaprokhyra.levelife.view.TaskAdapter;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;

public class TaskActivity extends AppCompatActivity {

    private int currentUserId;
    private MainViewModel viewModel;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddTask;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, getString(R.string.error_session_lost), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.init(currentUserId);

        initViews();
        setupObservers();
        setupNavigation();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvTasks);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(task -> {
            if (currentUser == null) return;
            
            boolean leveledUp = currentUser.addExperience(task.getRewardXP());
            showRewardToast(task.getRewardXP(), task.getRewardBerries());

            if (leveledUp) {
                showLevelUpDialog();
            }

            viewModel.completeTask(task, currentUser);
        });
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Task taskToDelete = adapter.getTaskAt(position);
                viewModel.deleteTask(taskToDelete);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            DialogUtils.showCreateTaskDialog(this, taskTitle -> {
                Task newTask = new Task(currentUserId, taskTitle, "", "General", 10, 10);
                viewModel.insertTask(newTask);
            });
        });
    }

    private void setupObservers() {
        viewModel.getUser().observe(this, user -> {
            this.currentUser = user;
        });

        viewModel.getUserTasks().observe(this, tasks -> {
            if (tasks == null || tasks.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);
                adapter.setTasks(tasks);
            }
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav == null) return;

        bottomNav.setItemIconTintList(null);
        bottomNav.setSelectedItemId(R.id.nav_tasks);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                finish();
                return true;
            } else if (itemId == R.id.nav_shop) {
                navigateTo(ShopActivity.class);
                return true;
            } else if (itemId == R.id.nav_inventory) {
                navigateTo(InventoryActivity.class);
                return true;
            } else if (itemId == R.id.nav_tasks) {
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
        finish();
    }

    private void performLogout() {
        getSharedPreferences("LeveLifeSession", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showRewardToast(int xp, int berries) {
        String msg = getString(R.string.reward_claimed, xp, berries);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showLevelUpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_levelup_title)
                .setMessage(getString(R.string.dialog_levelup_message, currentUser.getLevel()))
                .setPositiveButton(R.string.dialog_levelup_button, null)
                .show();
    }
}