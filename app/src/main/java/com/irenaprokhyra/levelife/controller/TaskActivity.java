package com.irenaprokhyra.levelife.controller;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.irenaprokhyra.levelife.R;

public class TaskActivity extends AppCompatActivity {

    private int currentUserId;

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

        // Aquí inicializaremos el RecyclerView en la próxima sesión
        // initViews();
        // setupRecyclerView();
        // loadTasks();
    }
}