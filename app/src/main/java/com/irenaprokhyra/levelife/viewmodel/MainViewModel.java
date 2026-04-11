package com.irenaprokhyra.levelife.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.PlacedFurnitureItem;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.model.Task;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainViewModel extends AndroidViewModel {
    private static final String FREQUENCY_HINT_PREFS = "LeveLife_Prefs";
    private static final String FREQUENCY_HINTS_USER_KEY_PREFIX = "shown_frequency_hints_user_";

    private final MainRepository repository;
    private int currentUserId = -1;
    private LiveData<User> user;
    private LiveData<List<Task>> userTasks;
    private LiveData<List<Furniture>> inventory;
    private LiveData<List<Furniture>> shopCatalog;
    private LiveData<List<PlacedFurnitureItem>> placedFurniture;
    private final MutableLiveData<String> errorMessages = new MutableLiveData<>();
    private final MutableLiveData<String> rewardMessage = new MutableLiveData<>();
    public static class FrequencyInfo {
        public final int titleRes;
        public final int messageRes;

        public FrequencyInfo(int titleRes, int messageRes) {
            this.titleRes = titleRes;
            this.messageRes = messageRes;
        }
    }

    private final MutableLiveData<FrequencyInfo> showFrequencyInfoDialog = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);
        repository = MainRepository.getInstance(application);
    }

    public void init(int userId) {
        this.currentUserId = userId;
        if (this.user == null) {
            user = repository.getUserLiveData(userId);
            userTasks = repository.getTasksLiveData(userId);
            inventory = repository.getUserInventory(userId);
            shopCatalog = repository.getShopCatalog();
            placedFurniture = repository.getPlacedFurnitureItemsForUserLiveData(userId);
        }
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<List<Task>> getUserTasks() {
        return userTasks;
    }

    public LiveData<List<Furniture>> getInventory() {
        return inventory;
    }

    public LiveData<List<Furniture>> getShopCatalog() {
        return shopCatalog;
    }

    public LiveData<List<PlacedFurnitureItem>> getPlacedFurniture() {
        return placedFurniture;
    }

    public LiveData<String> getErrorMessages() {
        return errorMessages;
    }

    public LiveData<String> getRewardMessage() {
        return rewardMessage;
    }

    public LiveData<FrequencyInfo> getShowFrequencyInfoDialog() {
        return showFrequencyInfoDialog;
    }

    public void completeTask(Task task) {
        if (task == null) return;

        User currentUser = user.getValue();
        if (currentUser == null) {
            errorMessages.postValue("Espera a que carguen tus datos");
            return;
        }

        repository.completeTask(task.getId(), currentUser.getId(), new MainRepository.TaskCompleteCallback() {
            @Override
            public void onSuccess(int rewardXP, int rewardBerries, int ecoReward, boolean leveledUp) {
                String message;
                if (ecoReward > 0) {
                    message = getApplication().getString(
                            R.string.reward_claimed_with_eco,
                            rewardXP,
                            rewardBerries,
                            ecoReward
                    );
                } else {
                    message = getApplication().getString(
                            R.string.reward_claimed,
                            rewardXP,
                            rewardBerries
                    );
                }
                rewardMessage.postValue(message);

                checkAndShowFrequencyInfo(task);
            }

            @Override
            public void onError(String message) {
                errorMessages.postValue(message);
            }
        });
    }

    private void checkAndShowFrequencyInfo(Task task) {
        if (currentUserId == -1 || task == null) {
            return;
        }

        String frequencyKey = getFrequencyHintValue(task.getFrequency());
        FrequencyInfo info = getFrequencyInfo(task.getFrequency());
        if (frequencyKey == null || info == null) {
            return;
        }

        android.content.SharedPreferences prefs = getFrequencyHintPreferences();
        String userKey = buildFrequencyHintsUserKey(currentUserId);
        Set<String> shownFrequencies = new HashSet<>(prefs.getStringSet(userKey, Collections.emptySet()));

        if (shownFrequencies.contains(frequencyKey)) {
            return;
        }

        shownFrequencies.add(frequencyKey);
        prefs.edit().putStringSet(userKey, shownFrequencies).commit();
        showFrequencyInfoDialog.postValue(info);
    }

    private FrequencyInfo getFrequencyInfo(String rawFrequency) {
        String frequency = Task.normalizeFrequency(rawFrequency);
        switch (frequency) {
            case Task.FREQUENCY_DAILY:
                return new FrequencyInfo(R.string.dialog_daily_task_reset_title, R.string.dialog_daily_task_reset_message);
            case Task.FREQUENCY_WEEKLY:
                return new FrequencyInfo(R.string.dialog_weekly_task_reset_title, R.string.dialog_weekly_task_reset_message);
            case Task.FREQUENCY_MONTHLY:
                return new FrequencyInfo(R.string.dialog_monthly_task_reset_title, R.string.dialog_monthly_task_reset_message);
            case Task.FREQUENCY_ONCE:
                return new FrequencyInfo(R.string.dialog_once_task_info_title, R.string.dialog_once_task_info_message);
            default:
                return null;
        }
    }

    private String getFrequencyHintValue(String rawFrequency) {
        String frequency = Task.normalizeFrequency(rawFrequency);
        switch (frequency) {
            case Task.FREQUENCY_DAILY:
                return "daily";
            case Task.FREQUENCY_WEEKLY:
                return "weekly";
            case Task.FREQUENCY_MONTHLY:
                return "monthly";
            case Task.FREQUENCY_ONCE:
                return "once";
            default:
                return null;
        }
    }

    private android.content.SharedPreferences getFrequencyHintPreferences() {
        return getApplication().getSharedPreferences(
                FREQUENCY_HINT_PREFS,
                android.content.Context.MODE_PRIVATE
        );
    }

    private String buildFrequencyHintsUserKey(int userId) {
        return FREQUENCY_HINTS_USER_KEY_PREFIX + userId;
    }

    public void clearFrequencyInfoDialog() {
        showFrequencyInfoDialog.postValue(null);
    }

    public void resetFrequencyInfoHints() {
        if (currentUserId == -1) {
            return;
        }

        getFrequencyHintPreferences()
                .edit()
                .remove(buildFrequencyHintsUserKey(currentUserId))
                .apply();
    }

    public void clearTaskCompletionMessage() {
        rewardMessage.setValue(null);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public void insertTask(Task task) {
        repository.insertTask(task);
    }

    public void updateTask(Task task) {
        repository.updateTask(task);
    }

    public void purchaseFurniture(Furniture furniture, Runnable onSuccess) {
        if (currentUserId == -1) {
            errorMessages.postValue("Sesión no válida");
            return;
        }

        repository.purchaseFurniture(currentUserId, furniture, new MainRepository.PurchaseCallback() {
            @Override
            public void onSuccess() {
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void onError(String message) {
                errorMessages.postValue(message);
            }
        });
    }

    public void placeFurniture(Furniture furniture, String slot, Runnable onSuccess) {
        if (currentUserId == -1) {
            errorMessages.postValue("Sesión no válida");
            return;
        }

        repository.placeFurniture(currentUserId, furniture, slot, new MainRepository.PlacementCallback() {
            @Override
            public void onSuccess() {
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void onError(String message) {
                errorMessages.postValue(message);
            }
        });
    }

    public void removePlacedFurniture(String slot, Runnable onSuccess) {
        if (currentUserId == -1) {
            errorMessages.postValue("Usuario no identificado");
            return;
        }

        repository.removePlacedFurniture(currentUserId, slot, new MainRepository.PlacementCallback() {
            @Override
            public void onSuccess() {
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void onError(String message) {
                errorMessages.postValue(message);
            }
        });
    }
}
