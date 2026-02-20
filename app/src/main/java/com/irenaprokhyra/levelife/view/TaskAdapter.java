package com.irenaprokhyra.levelife.view;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private final OnTaskActionListener listener;

    // Interfaz para comunicar el click a la Activity
    public interface OnTaskActionListener {
        void onTaskClick(Task task); // >_ CAMBIO: Corregido 'Clic' por 'Click' _<
    }

    public TaskAdapter(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // >_ CAMBIO: Ahora es 'static' para evitar fugas de memoria (Memory Leaks) _<
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvCategory;
        private final TextView tvReward;
        private final CheckBox cbCompleted;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvCategory = itemView.findViewById(R.id.tvTaskCategory);
            tvReward = itemView.findViewById(R.id.tvTaskReward);
            cbCompleted = itemView.findViewById(R.id.cbTaskCompleted);
        }

        public void bind(final Task task, final OnTaskActionListener listener) {
            tvTitle.setText(task.getTitle());
            tvCategory.setText(task.getCategory());

            // Usamos el string formateado del recurso (+%1$d XP | +%2$d 🍒)
            String rewardText = itemView.getContext().getString(
                    R.string.item_task_reward_format,
                    task.getRewardXP(),
                    task.getRewardBerries()
            );
            tvReward.setText(rewardText);

            // >_ MEJORA UX: Feedback Visual de Tarea Completada _<
            if (task.isCompleted()) {
                // Tacha el texto y lo pone un poco transparente
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setAlpha(0.5f);
            } else {
                // Quita el tachado y restaura la opacidad (vital por el reciclaje de vistas)
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTitle.setAlpha(1.0f);
            }

            // Importante: Quitamos el listener temporalmente para evitar disparos falsos al hacer scroll
            cbCompleted.setOnCheckedChangeListener(null);
            cbCompleted.setChecked(task.isCompleted());

            cbCompleted.setOnClickListener(v -> listener.onTaskClick(task));
        }
    }
}
