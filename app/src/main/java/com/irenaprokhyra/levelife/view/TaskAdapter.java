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
import com.irenaprokhyra.levelife.util.TaskRecurrenceUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private final OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        this.tasks.sort(Comparator.comparing(TaskRecurrenceUtils::isCompletedForCurrentPeriod));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public Task getTaskAt(int position) {
        return tasks.get(position);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvCategory;
        private final TextView tvMeta;
        private final TextView tvReward;
        private final CheckBox cbCompleted;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvCategory = itemView.findViewById(R.id.tvTaskCategory);
            tvMeta = itemView.findViewById(R.id.tvTaskMeta);
            tvReward = itemView.findViewById(R.id.tvTaskReward);
            cbCompleted = itemView.findViewById(R.id.cbTaskCompleted);
        }

        public void bind(final Task task, final OnTaskActionListener listener) {
            boolean completedForCurrentPeriod = TaskRecurrenceUtils.isCompletedForCurrentPeriod(task);

            tvTitle.setText(task.getTitle());
            tvCategory.setText(task.getCategory());
            tvMeta.setText(
                    itemView.getContext().getString(
                            R.string.task_meta_format,
                            task.getDifficulty(),
                            TaskRecurrenceUtils.getFrequencyLabel(task)
                    )
            );

            String rewardText;
            if (task.getEcoReward() > 0) {
                rewardText = itemView.getContext().getString(
                        R.string.item_task_reward_with_eco_format,
                        task.getRewardXP(),
                        task.getRewardBerries(),
                        task.getEcoReward()
                );
            } else {
                rewardText = itemView.getContext().getString(
                        R.string.item_task_reward_format,
                        task.getRewardXP(),
                        task.getRewardBerries()
                );
            }
            tvReward.setText(rewardText);

            // Aseguramos que el tachado se elimine siempre para evitar problemas de reciclaje
            tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            if (completedForCurrentPeriod) {
                int disableColor = ContextCompat.getColor(itemView.getContext(), R.color.third_text_logo);
                tvTitle.setTextColor(disableColor);
                tvCategory.setTextColor(disableColor);
                tvMeta.setTextColor(disableColor);
                tvReward.setTextColor(disableColor);
                cbCompleted.setEnabled(false);
                cbCompleted.setOnClickListener(null);
            } else {
                tvTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black));
                tvCategory.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.highlight));
                tvMeta.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.third_text_logo));
                tvReward.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.reward));
                cbCompleted.setEnabled(true);
                cbCompleted.setOnClickListener(v -> listener.onTaskClick(task));
            }

            cbCompleted.setOnCheckedChangeListener(null);
            cbCompleted.setChecked(completedForCurrentPeriod);
        }
    }
}
