package com.irenaprokhyra.levelife.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.model.TaskDraft;
import com.irenaprokhyra.levelife.model.TaskReward;

public final class DialogUtils {

    private DialogUtils() {
    }

    public interface OnTaskCreatedListener {
        void onTaskCreated(TaskDraft draft);
    }

    public static void showCreateTaskBottomSheet(Context context, OnTaskCreatedListener listener) {
        if (listener == null) {
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.LeveLife_BottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_create_task, null);
        dialog.setContentView(view);

        FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }

        view.setBackgroundResource(R.drawable.bg_bottom_sheet_surface);

        TextInputEditText etTaskTitle = view.findViewById(R.id.etTaskTitle);
        TextInputEditText etTaskDescription = view.findViewById(R.id.etTaskDescription);
        MaterialAutoCompleteTextView actTaskCategory = view.findViewById(R.id.actTaskCategory);
        MaterialAutoCompleteTextView actTaskDifficulty = view.findViewById(R.id.actTaskDifficulty);
        MaterialAutoCompleteTextView actTaskFrequency = view.findViewById(R.id.actTaskFrequency);
        TextView tvTaskRewardPreview = view.findViewById(R.id.tvTaskRewardPreview);
        MaterialButton btnCreateTask = view.findViewById(R.id.btnCreateTaskFromSheet);

        bindDropdown(context, actTaskCategory, R.array.task_categories, 0);
        bindDropdown(context, actTaskDifficulty, R.array.task_difficulties, 1);
        bindDropdown(context, actTaskFrequency, R.array.task_frequencies, 0);

        Runnable updateRewardPreview = () -> renderRewardPreview(
                context,
                tvTaskRewardPreview,
                resolveReward(actTaskCategory, actTaskDifficulty)
        );

        actTaskCategory.setOnItemClickListener((parent, dropdownView, position, id) -> updateRewardPreview.run());
        actTaskDifficulty.setOnItemClickListener((parent, dropdownView, position, id) -> updateRewardPreview.run());
        actTaskFrequency.setOnItemClickListener((parent, dropdownView, position, id) -> updateRewardPreview.run());

        TextWatcher rewardPreviewWatcher = new SimpleTextWatcher(updateRewardPreview);
        actTaskCategory.addTextChangedListener(rewardPreviewWatcher);
        actTaskDifficulty.addTextChangedListener(rewardPreviewWatcher);
        actTaskFrequency.addTextChangedListener(rewardPreviewWatcher);

        updateRewardPreview.run();

        btnCreateTask.setOnClickListener(v -> {
            String title = readText(etTaskTitle);
            String description = readText(etTaskDescription);
            String category = Task.normalizeCategory(readText(actTaskCategory));
            String difficulty = Task.normalizeDifficulty(readText(actTaskDifficulty));
            String frequency = Task.normalizeFrequency(readText(actTaskFrequency));

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
        new MaterialAlertDialogBuilder(context)
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
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_levelup_title)
                .setMessage(context.getString(R.string.dialog_levelup_message, newLevel))
                .setPositiveButton(R.string.dialog_levelup_button, null)
                .show();
    }

    private static void bindDropdown(
            Context context,
            MaterialAutoCompleteTextView dropdown,
            int arrayResId,
            int defaultIndex
    ) {
        String[] options = context.getResources().getStringArray(arrayResId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, options);
        dropdown.setAdapter(adapter);

        if (defaultIndex >= 0 && defaultIndex < options.length) {
            dropdown.setText(options[defaultIndex], false);
        }
    }

    private static TaskReward resolveReward(
            MaterialAutoCompleteTextView categoryView,
            MaterialAutoCompleteTextView difficultyView
    ) {
        String category = Task.normalizeCategory(readText(categoryView));
        String difficulty = Task.normalizeDifficulty(readText(difficultyView));
        return TaskRewardCalculator.calculateRewards(difficulty, category);
    }

    private static void renderRewardPreview(Context context, TextView rewardView, TaskReward reward) {
        if (reward.getEcoReward() > 0) {
            rewardView.setText(context.getString(
                    R.string.dialog_task_reward_preview_with_eco,
                    reward.getRewardXP(),
                    reward.getRewardBerries(),
                    reward.getEcoReward()
            ));
            return;
        }

        rewardView.setText(context.getString(
                R.string.dialog_task_reward_preview,
                reward.getRewardXP(),
                reward.getRewardBerries()
        ));
    }

    private static String readText(TextView view) {
        return view.getText() != null ? view.getText().toString().trim() : "";
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
