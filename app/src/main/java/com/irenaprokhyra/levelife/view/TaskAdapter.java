package com.irenaprokhyra.levelife.view;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

    // >_ METODO AUXILIAR PARA EL SWIPE _<
    public Task getTaskAt(int position) {
        return tasks.get(position);
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

            // Usamos el string formateado del recurso (+%1$d XP | +%2$d)
            String rewardText = itemView.getContext().getString(
                    R.string.item_task_reward_format,
                    task.getRewardXP(),
                    task.getRewardBerries()
            );
            tvReward.setText(rewardText);

            // >_ MEJORA UX: Feedback Visual de Tarea Completada _<
            if (task.isCompleted()) {
                // Cambiamos el texto y la opacidad a gris (deshabilitado)
                int disableColor = ContextCompat.getColor(itemView.getContext(), R.color.secondary_text);
                tvTitle.setTextColor(disableColor);
                tvCategory.setTextColor(disableColor);
                tvReward.setTextColor(disableColor);

                // Quitamos el tachado por si venia de antes
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                // Deshabilitamos el CheckBox y anulamos el click
                cbCompleted.setEnabled(false);
                cbCompleted.setOnClickListener(null);

            } else {
                // Restauramos los colores originales (Vital por el reciclaje de RecyclerView)
                tvTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black));
                tvCategory.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.secondary_text));
                tvReward.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.reward));

                // Habilitamos el CheckBox y su listener
                cbCompleted.setEnabled(true);
                cbCompleted.setOnClickListener(v -> listener.onTaskClick(task));
            }

            // Mantenemos el estado visual de la palomita
            cbCompleted.setOnCheckedChangeListener(null);
            cbCompleted.setChecked(task.isCompleted());
        }
    }
}
