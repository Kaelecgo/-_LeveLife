package com.irenaprokhyra.levelife.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        // >_ IMPORTANTE -> Cargamos perfil y tareas _<
        loadUserProfile();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvTasks);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el adaptador con la LÓGICA DE GAMIFICACIÓN
        adapter = new TaskAdapter(task -> {
            if (currentUser == null) return; // Protección por si no ha cargado el usuario

            // >_ SEGURIDAD | Como bloqueamos el uncheck en el Adapter,
            // este clic SIEMPRE será para completar la tarea de forma definitiva. _<
            task.setCompleted(true);

            boolean leveledUp = currentUser.addExperience(task.getRewardXP());
            currentUser.addBerries(task.getRewardBerries());

            // Feedback al usuario
            showRewardToast(task.getRewardXP(), task.getRewardBerries());

            if (leveledUp) {
                showLevelUpDialog();
            }

            // Guardar cambios de usuario en BD (dinero y XP)
            repository.updateUser(currentUser);

            // >_ FIX CONDICIÓN DE CARRERA: Guardamos la tarea y esperamos _<
            // Al pasarle 'this::loadTasks' como Runnable, le decimos al repositorio:
            // "Cuando termines de guardar esto en SQLite, y ni un milisegundo antes, recarga la lista".
            repository.updateTask(task, this::loadTasks);
        });
        recyclerView.setAdapter(adapter);

        // >_ CONFIGURACIÓN DEL SWIPE TO DELETE _<
        // El '0' significa que no permitimos arrastrar (Drag & Drop) hacia arriba/abajo.
        // LEFT | RIGHT permite deslizar la tarjeta hacia ambos lados.
        androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback simpleCallback =
                new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT | androidx.recyclerview.widget.ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false; // No hacemos nada al mover arriba o abajo
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        // Averiguamos qué posición de la lista se ha deslizado
                        int position = viewHolder.getAdapterPosition();

                        // Rescatamos el objeto Task correspondiente
                        Task taskToDelete = adapter.getTaskAt(position);

                        // Lo borramos de la BD y, cuando termine, recargamos la lista
                        // (Al recargar, la tarea desaparecerá visualmente y si la lista se queda vacía, saltará el Empty State automático)
                        repository.deleteTask(taskToDelete, TaskActivity.this::loadTasks);
                    }
                };
        // Enganchamos el detector de gestos a nuestra lista
        new androidx.recyclerview.widget.ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);


        fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            // Delegamos la creación visual a nuestra clase utilitaria
            com.irenaprokhyra.levelife.util.DialogUtils.showCreateTaskDialog(this, taskTitle -> {
                // Creamos la tarea con valores por defecto ágiles (MVP)
                Task newTask = new Task(currentUserId, taskTitle, "", "General", 10, 10);

                // La guardamos en la BD y esperamos confirmación para recargar
                repository.insertTask(newTask, this ::loadTasks);
            });
        });

    }

    // >_ CARGA DEL PERFIL (Necesario para sumar XP) _<
    private void loadUserProfile() {
        repository.getUserById(currentUserId, new MainRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                // Una vez tenemos al usuario, cargamos sus tareas
                loadTasks();
            }

            @Override
            public void onError(String message) {
                // >_ CAMBIO: Usamos string de recursos _<
                runOnUiThread(() -> Toast.makeText(TaskActivity.this, getString(R.string.error_load_user), Toast.LENGTH_SHORT).show());
            }
        });
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