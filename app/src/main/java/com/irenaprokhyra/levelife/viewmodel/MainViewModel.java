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
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final MainRepository repository;
    private int currentUserId = -1;
    private LiveData<User> user;
    private LiveData<List<Task>> userTasks;
    private LiveData<List<Furniture>> inventory;
    private LiveData<List<Furniture>> shopCatalog;
    private LiveData<List<PlacedFurnitureItem>> placedFurniture;
    private final MutableLiveData<String> errorMessages = new MutableLiveData<>();
    private final MutableLiveData<String> rewardMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showDailyTaskResetDialog = new MutableLiveData<>();

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

    public LiveData<Boolean> getShowDailyTaskResetDialog() {
        return showDailyTaskResetDialog;
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

                if (task.isRecurring() && Task.FREQUENCY_DAILY.equals(Task.normalizeFrequency(task.getFrequency()))) {
                    showDailyTaskResetDialog.postValue(true);
                }
            }

            @Override
            public void onError(String message) {
                errorMessages.postValue(message);
            }
        });
    }

    public void clearTaskCompletionMessage() {
        rewardMessage.setValue(null);
    }

    public void clearDailyTaskResetDialog() {
        showDailyTaskResetDialog.setValue(null);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public void insertTask(Task task) {
        repository.insertTask(task);
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
