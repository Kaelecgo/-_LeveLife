package com.irenaprokhyra.levelife.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.model.PlacedFurniture;
import com.irenaprokhyra.levelife.model.Task;
import com.irenaprokhyra.levelife.model.TaskDraft;
import com.irenaprokhyra.levelife.model.TaskReward;

public final class DialogUtils {

    private DialogUtils() {
    }

    private static final int MATERIAL_BOTTOM_SHEET_ID = resolveMaterialBottomSheetId();

    public interface OnTaskCreatedListener {
        void onTaskCreated(TaskDraft draft);
    }

    public interface OnFurnitureSlotSelectedListener {
        void onSlotSelected(String slot);
    }

    public static void showCreateTaskBottomSheet(Context context, OnTaskCreatedListener listener) {
        showTaskBottomSheet(
                context,
                null,
                R.string.tasks_dialog_new_title,
                R.string.common_action_create,
                listener
        );
    }

    public static void showEditTaskBottomSheet(
            Context context,
            TaskDraft initialDraft,
            OnTaskCreatedListener listener
    ) {
        showTaskBottomSheet(
                context,
                initialDraft,
                R.string.tasks_dialog_edit_title,
                R.string.common_action_save,
                listener
        );
    }

    private static void showTaskBottomSheet(
            Context context,
            TaskDraft initialDraft,
            int titleResId,
            int actionResId,
            OnTaskCreatedListener listener
    ) {
        if (listener == null) {
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.LeveLife_BottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_create_task, null);
        dialog.setContentView(view);

        FrameLayout bottomSheet = dialog.findViewById(MATERIAL_BOTTOM_SHEET_ID);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }

        view.setBackgroundResource(R.drawable.bg_bottom_sheet_surface);

        TextInputEditText etTaskTitle = view.findViewById(R.id.etTaskTitle);
        TextInputEditText etTaskDescription = view.findViewById(R.id.etTaskDescription);
        MaterialAutoCompleteTextView actTaskCategory = view.findViewById(R.id.actTaskCategory);
        MaterialAutoCompleteTextView actTaskDifficulty = view.findViewById(R.id.actTaskDifficulty);
        MaterialAutoCompleteTextView actTaskFrequency = view.findViewById(R.id.actTaskFrequency);
        TextView tvTaskSheetTitle = view.findViewById(R.id.tvTaskSheetTitle);
        TextView tvRewardXP = view.findViewById(R.id.tvRewardXP);
        TextView tvRewardBerries = view.findViewById(R.id.tvRewardBerries);
        TextView tvRewardEco = view.findViewById(R.id.tvRewardEco);
        View layoutEcoReward = view.findViewById(R.id.layoutEcoReward);
        MaterialButton btnCreateTask = view.findViewById(R.id.btnCreateTaskFromSheet);

        bindDropdown(context, actTaskCategory, R.array.task_categories, 0);
        bindDropdown(context, actTaskDifficulty, R.array.task_difficulties, 1);
        bindDropdown(context, actTaskFrequency, R.array.task_frequencies, 0);
        tvTaskSheetTitle.setText(titleResId);
        btnCreateTask.setText(actionResId);

        if (initialDraft != null) {
            etTaskTitle.setText(initialDraft.getTitle());
            etTaskDescription.setText(initialDraft.getDescription());
            actTaskCategory.setText(initialDraft.getCategory(), false);
            actTaskDifficulty.setText(initialDraft.getDifficulty(), false);
            actTaskFrequency.setText(initialDraft.getFrequency(), false);
        }

        Runnable updateRewardPreview = () -> renderRewardPreview(
                context,
                tvRewardXP,
                tvRewardBerries,
                tvRewardEco,
                layoutEcoReward,
                resolveReward(actTaskCategory, actTaskDifficulty)
        );

        actTaskCategory.setOnItemClickListener((parent, dropdownView, position, id) -> updateRewardPreview.run());
        actTaskDifficulty.setOnItemClickListener((parent, dropdownView, position, id) -> updateRewardPreview.run());

        TextWatcher rewardPreviewWatcher = new SimpleTextWatcher(updateRewardPreview);
        actTaskCategory.addTextChangedListener(rewardPreviewWatcher);
        actTaskDifficulty.addTextChangedListener(rewardPreviewWatcher);

        updateRewardPreview.run();

        btnCreateTask.setOnClickListener(v -> {
            String title = readText(etTaskTitle);
            String description = readText(etTaskDescription);
            String category = Task.normalizeCategory(readText(actTaskCategory));
            String difficulty = Task.normalizeDifficulty(readText(actTaskDifficulty));
            String frequency = Task.normalizeFrequency(readText(actTaskFrequency));

            if (title.isEmpty()
                    || !Task.isKnownCategory(category)
                    || !Task.isKnownDifficulty(difficulty)
                    || !Task.isKnownFrequency(frequency)) {
                Toast.makeText(context, R.string.error_invalid_task_form, Toast.LENGTH_SHORT).show();
                return;
            }

            TaskReward reward = TaskRewardCalculator.calculateRewards(difficulty, category);
            listener.onTaskCreated(new TaskDraft(title, description, category, difficulty, frequency, reward));
            dialog.dismiss();
        });

        dialog.show();
    }

    public static void showDeleteTaskConfirmationDialog(Context context, Runnable onConfirm) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.tasks_dialog_delete_title)
                .setMessage(R.string.tasks_dialog_delete_message)
                .setPositiveButton(R.string.common_action_delete, (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton(R.string.common_action_cancel, null)
                .show();
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

    public static void showTaskFrequencyInfoDialog(Context context, int titleRes, int messageRes) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.LeveLife_BottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_daily_reset_info, null);
        dialog.setContentView(view);

        FrameLayout bottomSheet = dialog.findViewById(MATERIAL_BOTTOM_SHEET_ID);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }
        view.setBackgroundResource(R.drawable.bg_bottom_sheet_surface);

        TextView tvTitle = view.findViewById(R.id.tvDailyResetTitle);
        TextView tvMessage = view.findViewById(R.id.tvDailyResetMessage);
        MaterialButton btnUnderstood = view.findViewById(R.id.btnUnderstood);

        tvTitle.setText(titleRes);
        tvMessage.setText(messageRes);
        btnUnderstood.setText(R.string.dialog_task_reset_button);

        btnUnderstood.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static void showFurnitureSlotPickerBottomSheet(
            Context context,
            Furniture furniture,
            OnFurnitureSlotSelectedListener listener
    ) {
        if (context == null || furniture == null || listener == null) {
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.LeveLife_BottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_furniture_slot_picker, null);
        dialog.setContentView(view);

        FrameLayout bottomSheet = dialog.findViewById(MATERIAL_BOTTOM_SHEET_ID);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }
        view.setBackgroundResource(R.drawable.bg_bottom_sheet_surface);

        ImageView ivFurniturePreview = view.findViewById(R.id.ivFurniturePreview);
        TextView tvFurnitureName = view.findViewById(R.id.tvFurniturePreviewName);
        MaterialButton btnFloor = view.findViewById(R.id.btnSlotFloor);
        MaterialButton btnWall = view.findViewById(R.id.btnSlotWall);
        MaterialButton btnDesk = view.findViewById(R.id.btnSlotDesk);
        MaterialButton btnDecor = view.findViewById(R.id.btnSlotDecor);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelSlotPicker);

        int drawableResId = FurnitureDrawableResolver.resolveDrawableResId(context, furniture.getImageRef());
        ivFurniturePreview.setImageResource(drawableResId);
        ivFurniturePreview.setContentDescription(furniture.getName());
        tvFurnitureName.setText(furniture.getName());

        bindSlotButton(btnFloor, PlacedFurniture.SLOT_FLOOR, listener, dialog);
        bindSlotButton(btnWall, PlacedFurniture.SLOT_WALL, listener, dialog);
        bindSlotButton(btnDesk, PlacedFurniture.SLOT_DESK, listener, dialog);
        bindSlotButton(btnDecor, PlacedFurniture.SLOT_DECOR, listener, dialog);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static void showReplaceFurnitureConfirmationDialog(
            Context context,
            String slotLabel,
            String currentFurnitureName,
            String nextFurnitureName,
            Runnable onConfirm
    ) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.inventory_replace_slot_title)
                .setMessage(context.getString(
                        R.string.inventory_replace_slot_message,
                        slotLabel,
                        currentFurnitureName,
                        nextFurnitureName
                ))
                .setPositiveButton(R.string.common_action_continue, (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton(R.string.common_action_cancel, null)
                .show();
    }

    public static void showPlacedFurnitureManagementDialog(
            Context context,
            String furnitureName,
            Runnable onMove,
            Runnable onRemove
    ) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.inventory_manage_furniture_title)
                .setMessage(context.getString(R.string.inventory_manage_furniture_message, furnitureName))
                .setPositiveButton(R.string.inventory_manage_action_move, (dialog, which) -> {
                    if (onMove != null) {
                        onMove.run();
                    }
                })
                .setNeutralButton(R.string.inventory_manage_action_remove, (dialog, which) -> {
                    if (onRemove != null) {
                        onRemove.run();
                    }
                })
                .setNegativeButton(R.string.common_action_cancel, null)
                .show();
    }

    public static void showRemovePlacedFurnitureConfirmationDialog(
            Context context,
            String furnitureName,
            Runnable onConfirm
    ) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.inventory_remove_furniture_title)
                .setMessage(context.getString(R.string.inventory_remove_furniture_message, furnitureName))
                .setPositiveButton(R.string.common_action_delete, (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton(R.string.common_action_cancel, null)
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

    private static void renderRewardPreview(
            Context context,
            TextView tvXP,
            TextView tvBerries,
            TextView tvEco,
            View layoutEco,
            TaskReward reward
    ) {
        tvXP.setText(String.format(java.util.Locale.getDefault(), "%d XP", reward.getRewardXP()));
        tvBerries.setText(String.format(java.util.Locale.getDefault(), "%d %s", reward.getRewardBerries(), context.getString(R.string.item_task_reward_berries_unit)));

        if (reward.getEcoReward() > 0) {
            layoutEco.setVisibility(View.VISIBLE);
            tvEco.setText(String.format(java.util.Locale.getDefault(), "%d Eco", reward.getEcoReward()));
        } else {
            layoutEco.setVisibility(View.GONE);
        }
    }

    private static String readText(TextView view) {
        return view.getText() != null ? view.getText().toString().trim() : "";
    }

    private static int resolveMaterialBottomSheetId() {
        return Resources.getSystem().getIdentifier(
                "design_bottom_sheet",
                "id",
                "com.google.android.material"
        );
    }

    private static void bindSlotButton(
            MaterialButton button,
            String slot,
            OnFurnitureSlotSelectedListener listener,
            BottomSheetDialog dialog
    ) {
        button.setOnClickListener(v -> {
            listener.onSlotSelected(slot);
            dialog.dismiss();
        });
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
