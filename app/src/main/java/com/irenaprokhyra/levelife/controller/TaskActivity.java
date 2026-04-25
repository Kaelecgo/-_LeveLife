package com.irenaprokhyra.levelife.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.TaskDraft;
import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.util.DialogUtils;
import com.irenaprokhyra.levelife.util.FeedbackUtils;
import com.irenaprokhyra.levelife.view.TaskAdapter;
import com.irenaprokhyra.levelife.viewmodel.MainViewModel;

public class TaskActivity extends AppCompatActivity {

    private int currentUserId;
    private MainViewModel viewModel;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddTask;
    private MaterialButton btnTaskEconomyGuideAction;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, getString(R.string.common_error_session_lost), Toast.LENGTH_SHORT).show();
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
        btnTaskEconomyGuideAction = findViewById(R.id.btnTaskEconomyGuideAction);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onTaskComplete(Task task) {
                if (currentUser == null) {
                    return;
                }
                viewModel.completeTask(task);
            }

            @Override
            public void onTaskEdit(Task task) {
                showEditTaskDialog(task);
            }
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
                adapter.notifyItemChanged(position);
                DialogUtils.showDeleteTaskConfirmationDialog(
                        TaskActivity.this,
                        () -> {
                            viewModel.deleteTask(taskToDelete);
                            Toast.makeText(
                                    TaskActivity.this,
                                    getString(R.string.tasks_deleted_message, taskToDelete.getTitle()),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            DialogUtils.showCreateTaskBottomSheet(this, draft -> {
                Task newTask = buildTaskFromDraft(draft);
                viewModel.insertTask(newTask);
            });
        });
        fabAddTask.setOnLongClickListener(v -> {
            viewModel.resetFrequencyInfoHints();
            Toast.makeText(this, getString(R.string.tasks_frequency_feedbacks_reset), Toast.LENGTH_SHORT).show();
            return true;
        });

        btnTaskEconomyGuideAction.setOnClickListener(v -> navigateTo(ShopActivity.class));
    }

    private Task buildTaskFromDraft(TaskDraft draft) {
        return new Task(
                currentUserId,
                draft.getTitle(),
                draft.getDescription(),
                draft.getCategory(),
                draft.getReward().getRewardXP(),
                draft.getReward().getRewardBerries(),
                draft.getReward().getEcoReward(),
                draft.getDifficulty(),
                draft.getFrequency(),
                draft.isEcoTask()
        );
    }

    private void showEditTaskDialog(Task task) {
        if (task == null) {
            return;
        }

        DialogUtils.showEditTaskBottomSheet(this, TaskDraft.fromTask(task), draft -> {
            updateTaskFromDraft(task, draft);
            viewModel.updateTask(task);
            Toast.makeText(
                    this,
                    getString(R.string.tasks_updated_message, task.getTitle()),
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void updateTaskFromDraft(Task task, TaskDraft draft) {
        task.setTitle(draft.getTitle());
        task.setDescription(draft.getDescription());
        task.setCategory(draft.getCategory());
        task.setDifficulty(draft.getDifficulty());
        task.setFrequency(draft.getFrequency());
        task.setRewardXP(draft.getReward().getRewardXP());
        task.setRewardBerries(draft.getReward().getRewardBerries());
        task.setEcoReward(draft.getReward().getEcoReward());
        task.setEcoTask(draft.isEcoTask());
    }

    private void setupObservers() {
        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                // Si el nivel ha subido respecto al que teníamos guardado, mostramos el diálogo
                if (currentUser != null && user.getLevel() > currentUser.getLevel()) {
                    FeedbackUtils.playLevelUpFeedback(recyclerView);
                    DialogUtils.showLevelUpDialog(this, user.getLevel());
                }
                this.currentUser = user;
            }
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

        // Nuevo observador para el mensaje de éxito de la tarea
        viewModel.getRewardMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                FeedbackUtils.playTaskCompletedFeedback(recyclerView);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                // Limpiamos el mensaje para que no se repita al rotar la pantalla
                viewModel.clearTaskCompletionMessage();
            }
        });

        viewModel.getShowFrequencyInfoDialog().observe(this, info -> {
            if (info != null) {
                DialogUtils.showTaskFrequencyInfoDialog(this, info.titleRes, info.messageRes);
                viewModel.clearFrequencyInfoDialog();
            }
        });

        viewModel.getErrorMessages().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav == null) return;

        //bottomNav.setItemIconTintList(null);
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
}
