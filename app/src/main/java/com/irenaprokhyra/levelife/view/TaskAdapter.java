package com.irenaprokhyra.levelife.view;

import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
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
    private static final Object TIMER_PAYLOAD = new Object();
    private static final long TIMER_TICK_MS = 1000L;

    private List<Task> tasks = new ArrayList<>();
    private final OnTaskActionListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerTicker = new Runnable() {
        @Override
        public void run() {
            if (!hasActiveRecurringTimers()) {
                tickerRunning = false;
                return;
            }

            if (!tasks.isEmpty()) {
                notifyItemRangeChanged(0, tasks.size(), TIMER_PAYLOAD);
            }
            mainHandler.postDelayed(this, TIMER_TICK_MS);
        }
    };
    private boolean tickerRunning;

    public interface OnTaskActionListener {
        void onTaskComplete(Task task);
        void onTaskEdit(Task task);
    }

    public TaskAdapter(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        this.tasks.sort(Comparator.comparing(TaskRecurrenceUtils::isCompletedForCurrentPeriod));
        notifyDataSetChanged();
        syncTickerState();
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
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains(TIMER_PAYLOAD)) {
            holder.bindTimer(tasks.get(position));
            return;
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onViewRecycled(@NonNull TaskViewHolder holder) {
        holder.resetTimerView();
        super.onViewRecycled(holder);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        syncTickerState();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        stopTicker();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public Task getTaskAt(int position) {
        return tasks.get(position);
    }

    private boolean hasActiveRecurringTimers() {
        for (Task task : tasks) {
            if (task.isRecurring() && TaskRecurrenceUtils.isCompletedForCurrentPeriod(task)) {
                return true;
            }
        }
        return false;
    }

    private void syncTickerState() {
        if (hasActiveRecurringTimers()) {
            startTicker();
        } else {
            stopTicker();
        }
    }

    private void startTicker() {
        if (tickerRunning) {
            return;
        }
        tickerRunning = true;
        mainHandler.post(timerTicker);
    }

    private void stopTicker() {
        if (!tickerRunning) {
            return;
        }
        tickerRunning = false;
        mainHandler.removeCallbacks(timerTicker);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvCategory;
        private final TextView tvMeta;
        private final TextView tvResetTimer;
        private final CheckBox cbCompleted;

        private final TextView tvRewardXp;
        private final TextView tvRewardBerries;
        private final TextView tvRewardEco;
        private final ImageView ivRewardXpIcon;
        private final ImageView ivRewardBerriesIcon;
        private final ImageView ivRewardEcoIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvCategory = itemView.findViewById(R.id.tvTaskCategory);
            tvMeta = itemView.findViewById(R.id.tvTaskMeta);
            tvResetTimer = itemView.findViewById(R.id.tvTaskResetTimer);
            cbCompleted = itemView.findViewById(R.id.cbTaskCompleted);

            tvRewardXp = itemView.findViewById(R.id.tvRewardXp);
            tvRewardBerries = itemView.findViewById(R.id.tvRewardBerries);
            tvRewardEco = itemView.findViewById(R.id.tvRewardEco);
            ivRewardXpIcon = itemView.findViewById(R.id.ivRewardXpIcon);
            ivRewardBerriesIcon = itemView.findViewById(R.id.ivRewardBerriesIcon);
            ivRewardEcoIcon = itemView.findViewById(R.id.ivRewardEcoIcon);
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

            tvRewardXp.setText(String.format(java.util.Locale.getDefault(), "+%d", task.getRewardXP()));
            tvRewardBerries.setText(String.format(java.util.Locale.getDefault(), "+%d", task.getRewardBerries()));

            if (task.getEcoReward() > 0) {
                tvRewardEco.setVisibility(View.VISIBLE);
                ivRewardEcoIcon.setVisibility(View.VISIBLE);
                tvRewardEco.setText(String.format(java.util.Locale.getDefault(), "+%d", task.getEcoReward()));
            } else {
                tvRewardEco.setVisibility(View.GONE);
                ivRewardEcoIcon.setVisibility(View.GONE);
            }

            tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            if (completedForCurrentPeriod) {
                int disableColor = ContextCompat.getColor(itemView.getContext(), R.color.third_text_logo);
                tvTitle.setTextColor(disableColor);
                tvCategory.setTextColor(disableColor);
                tvMeta.setTextColor(disableColor);

                tvRewardXp.setTextColor(disableColor);
                tvRewardBerries.setTextColor(disableColor);
                tvRewardEco.setTextColor(disableColor);

                ivRewardXpIcon.setColorFilter(disableColor);
                ivRewardBerriesIcon.setColorFilter(disableColor);
                ivRewardEcoIcon.setColorFilter(disableColor);

                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                cbCompleted.setEnabled(false);
                cbCompleted.setOnClickListener(null);
            } else {
                tvTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black));
                tvCategory.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.highlight));
                tvMeta.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.third_text_logo));

                tvRewardXp.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.highlight));
                tvRewardBerries.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.game_berries));
                tvRewardEco.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.brand_green_dark));

                ivRewardXpIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.highlight));
                ivRewardBerriesIcon.clearColorFilter();
                ivRewardEcoIcon.clearColorFilter();

                cbCompleted.setEnabled(true);
                cbCompleted.setOnClickListener(v -> listener.onTaskComplete(task));
                tvResetTimer.setVisibility(View.GONE);
            }

            cbCompleted.setOnCheckedChangeListener(null);
            cbCompleted.setChecked(completedForCurrentPeriod);
            itemView.setOnClickListener(v -> listener.onTaskEdit(task));
            bindTimer(task);
        }

        public void bindTimer(Task task) {
            if (!task.isRecurring() || !TaskRecurrenceUtils.isCompletedForCurrentPeriod(task)) {
                resetTimerView();
                return;
            }

            long millisLeft = TaskRecurrenceUtils.getMillisUntilNextPeriod(
                    task.getFrequency(),
                    System.currentTimeMillis()
            );

            if (millisLeft <= 0L) {
                resetTimerView();
                return;
            }

            tvResetTimer.setVisibility(View.VISIBLE);
            tvResetTimer.setText(itemView.getContext().getString(
                    R.string.task_reset_in_format,
                    TaskRecurrenceUtils.formatRemainingTime(millisLeft)
            ));
        }

        public void resetTimerView() {
            tvResetTimer.setVisibility(View.GONE);
            tvResetTimer.setText(null);
        }
    }
}
