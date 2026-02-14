package com.irenaprokhyra.levelife.controller;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.view.TaskAdapter;

import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private int currentUserId;
    private MainRepository repository;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;


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
        loadTasks();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el adaptador
        adapter = new TaskAdapter(new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onTaskClic(Task task) {
                // AQUÍ irá la lógica de gamificación en la próxima sesión (Sesión 3)
                // Por ahora, solo mostramos que funciona el click
                Toast.makeText(TaskActivity.this, "Click en la tarea: " + task.getTitle(), Toast.LENGTH_SHORT).show();

                // Pequeño truco visual: invertimos el estado localmente para ver el check moverse
                task.toggleCompleted();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadTasks() {
        // Pedimos al repositorio las tareas de ESTE usuario
        repository.getTaskForUser(currentUserId, new MainRepository.TaskListCallback() {

            @Override
            public void onSuccess(List<Task> tasks) {
                runOnUiThread(() -> {
                    // Pasamos la lista al adaptador para que la pinte
                    adapter.setTasks(tasks);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                    Toast.makeText(TaskActivity.this, getString(R.string.error_load_tasks), Toast.LENGTH_SHORT).show());
            }
        });
    }
}