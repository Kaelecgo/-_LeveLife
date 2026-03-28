package com.irenaprokhyra.levelife.util;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.irenaprokhyra.levelife.R;

public class DialogUtils {

    public interface OnTaskCreatedListener {
        void onTaskCreated(String taskTitle);
    }

    public static void showCreateTaskDialog(Context context, OnTaskCreatedListener listener) {
        // Creamos el campo de texto por código
        final EditText input = new EditText(context);
        input.setHint(R.string.hint_task_title);

        new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_new_task_title)
                .setView(input)
                .setPositiveButton(R.string.btn_create, (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if(!title.isEmpty()) {
                        listener.onTaskCreated(title);
                    } else {
                        Toast.makeText(context, R.string.error_empty_task, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialog_no, null)
                .show();
    }

    // >_ Diálogo de confirmación para 'cerrar sesión' _<
    public static void showLogoutConfirmationDialog(Context context, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.dialog_yes, (dialog, which) -> {
                    // Si pulsa "Sí, salir", ejecutamos la acción que nos mande la Activity
                    if (onConfirm != null) onConfirm.run();
                })
                .setNegativeButton(R.string.dialog_no, null)
                .show();// Null cierra el diálogo sin hacer nada
    }

    /**
     * Muestra un diálogo de felicitación cuando el usuario sube de nivel.
     */
    public static void showLevelUpDialog(Context context, int newLevel) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_levelup_title)
                .setMessage(context.getString(R.string.dialog_levelup_message, newLevel))
                .setPositiveButton(R.string.dialog_levelup_button, null)
                .show();
    }
}
