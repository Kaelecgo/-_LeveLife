package com.irenaprokhyra.levelife.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.model.TaskDraft;
import com.irenaprokhyra.levelife.model.TaskReward;

public class DialogUtils {

    public interface OnTaskCreatedListener {
        void onTaskCreated(TaskDraft draft);
    }

    public static void showCreateTaskDialog(Context context, OnTaskCreatedListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_create_task, null);
        dialog.setContentView(view);

        TextInputEditText etTaskTitle = view.findViewById(R.id.etTaskTitle);
        TextInputEditText etTaskDescription = view.findViewById(R.id.etTaskDescription);
        MaterialAutoCompleteTextView actTaskCategory = view.findViewById(R.id.actTaskCategory);
        MaterialAutoCompleteTextView actTaskDifficulty = view.findViewById(R.id.actTaskDifficulty);
        MaterialAutoCompleteTextView actTaskFrequency = view.findViewById(R.id.actTaskFrequency);
        TextView tvTaskRewardPreview = view.findViewById(R.id.tvTaskRewardPreview);
        MaterialButton btnCreateTask = view.findViewById(R.id.btnCreateTaskFromSheet);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                context.getResources().getStringArray(R.array.task_categories)
        );
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                context.getResources().getStringArray(R.array.task_difficulties)
        );
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                context.getResources().getStringArray(R.array.task_frequencies)
        );

        actTaskCategory.setAdapter(categoryAdapter);
        actTaskDifficulty.setAdapter(difficultyAdapter);
        actTaskFrequency.setAdapter(frequencyAdapter);

        actTaskCategory.setText(categoryAdapter.getItem(0), false);
        actTaskDifficulty.setText(difficultyAdapter.getItem(1), false);
        actTaskFrequency.setText(frequencyAdapter.getItem(0), false);

        Runnable updateRewardPreview = () -> {
            String selectedCategory = Task.normalizeCategory(
                    actTaskCategory.getText() != null ? actTaskCategory.getText().toString().trim() : ""
            );
            String selectedDifficulty = Task.normalizeDifficulty(
                    actTaskDifficulty.getText() != null ? actTaskDifficulty.getText().toString().trim() : ""
            );

            TaskReward reward = TaskRewardCalculator.calculateRewards(selectedDifficulty, selectedCategory);
            if (reward.getEcoReward() > 0) {
                tvTaskRewardPreview.setText(
                        context.getString(
                                R.string.dialog_task_reward_preview_with_eco,
                                reward.getRewardXP(),
                                reward.getRewardBerries(),
                                reward.getEcoReward()
                        )
                );
            } else {
                tvTaskRewardPreview.setText(
                        context.getString(
                                R.string.dialog_task_reward_preview,
                                reward.getRewardXP(),
                                reward.getRewardBerries()
                        )
                );
            }
        };

        actTaskCategory.setOnItemClickListener((parent, view1, position, id) -> updateRewardPreview.run());
        actTaskDifficulty.setOnItemClickListener((parent, view12, position, id) -> updateRewardPreview.run());
        actTaskFrequency.setOnItemClickListener((parent, view13, position, id) -> updateRewardPreview.run());
        TextWatcher rewardPreviewWatcher = new SimpleTextWatcher(updateRewardPreview);
        actTaskCategory.addTextChangedListener(rewardPreviewWatcher);
        actTaskDifficulty.addTextChangedListener(rewardPreviewWatcher);
        actTaskFrequency.addTextChangedListener(rewardPreviewWatcher);

        updateRewardPreview.run();

        btnCreateTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText() != null ? etTaskTitle.getText().toString().trim() : "";
            String description = etTaskDescription.getText() != null ? etTaskDescription.getText().toString().trim() : "";
            String category = Task.normalizeCategory(
                    actTaskCategory.getText() != null ? actTaskCategory.getText().toString().trim() : ""
            );
            String difficulty = Task.normalizeDifficulty(
                    actTaskDifficulty.getText() != null ? actTaskDifficulty.getText().toString().trim() : ""
            );
            String frequency = Task.normalizeFrequency(
                    actTaskFrequency.getText() != null ? actTaskFrequency.getText().toString().trim() : ""
            );

            if (title.isEmpty() || category.isEmpty() || difficulty.isEmpty() || frequency.isEmpty()) {
                Toast.makeText(context, R.string.error_invalid_task_form, Toast.LENGTH_SHORT).show();
                return;
            }

            TaskReward reward = TaskRewardCalculator.calculateRewards(difficulty, category);
            listener.onTaskCreated(new TaskDraft(title, description, category, difficulty, frequency, reward));
            dialog.dismiss();
        });

        dialog.show();
    }

    public static void showLogoutConfirmationDialog(Context context, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.common_action_logout)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.common_action_yes, (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton(R.string.common_action_no, null)
                .show();
    }

    public static void showLevelUpDialog(Context context, int newLevel) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_levelup_title)
                .setMessage(context.getString(R.string.dialog_levelup_message, newLevel))
                .setPositiveButton(R.string.dialog_levelup_button, null)
                .show();
    }

    private static final class SimpleTextWatcher implements TextWatcher {
        private final Runnable onTextChanged;

        private SimpleTextWatcher(Runnable onTextChanged) {
            this.onTextChanged = onTextChanged;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (onTextChanged != null) {
                onTextChanged.run();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
