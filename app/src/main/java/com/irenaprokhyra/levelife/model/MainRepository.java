package com.irenaprokhyra.levelife.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.irenaprokhyra.levelife.util.PasswordUtils;
import com.irenaprokhyra.levelife.util.TaskRecurrenceUtils;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MainRepository {
    private static final int WELCOME_BERRIES = 50;
    private static volatile MainRepository instance;

    private final AppDatabase db;
    private final UserDao userDao;
    private final TaskDao taskDao;
    private final FurnitureDao furnitureDao;
    private final ExecutorService executor;

    private MainRepository(Application application) {
        db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        taskDao = db.taskDao();
        furnitureDao = db.furnitureDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public static synchronized MainRepository getInstance(Application application) {
        if (instance == null) { instance = new MainRepository(application); }
        return instance;
    }

    public LiveData<User> getUserLiveData(int userId) {
        return userDao.getUserByIdLiveData(userId);
    }

    public void loginUser(String username, String password, LoginCallback callback) {
        executor.execute(() -> {
            User user = userDao.getUserByUsername(username);
            if (user == null || !PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                callback.onError("Usuario o contrasena incorrectos");
                return;
            }

            if (PasswordUtils.needsUpgrade(user.getPasswordHash())) {
                user.setPasswordHash(PasswordUtils.hashPassword(password));
                userDao.updateUser(user);
            }

            callback.onSuccess(user);
        });
    }

    public void getUserById(int userId, LoginCallback callback) {
        executor.execute(() -> {
            User user = userDao.getUserById(userId);
            if (user != null) {
                callback.onSuccess(user);
            } else {
                callback.onError("Usuario no encontrado");
            }
        });
    }

    public void checkUserExists(String username, BooleanCallback callback) {
        executor.execute(() -> {
            int count = userDao.checkUserExists(username);
            callback.onResult(count > 0);
        });
    }

    public void insertUser(User user) {
        executor.execute(() -> {
            if (user.getPasswordHash() != null && PasswordUtils.needsUpgrade(user.getPasswordHash())) {
                user.setPasswordHash(PasswordUtils.hashPassword(user.getPasswordHash()));
            }
            userDao.insertUser(user);
        });
    }

    public void updateUser(User user) {
        executor.execute(() -> userDao.updateUser(user));
    }

    public void registerUser(String username, String rawPassword, RegistrationCallback callback) {
        executor.execute(() -> {
            try {
                Integer createdUserId = db.runInTransaction(() -> {
                    if (userDao.getUserByUsername(username) != null) {
                        return -1;
                    }

                    User newUser = new User(username, PasswordUtils.hashPassword(rawPassword));
                    newUser.setBerries(WELCOME_BERRIES);

                    long insertedId = userDao.insertUser(newUser);
                    if (insertedId <= 0) {
                        throw new IllegalStateException("No se pudo crear el usuario");
                    }

                    seedStarterTasks((int) insertedId);
                    return (int) insertedId;
                });

                if (createdUserId != null && createdUserId > 0) {
                    callback.onSuccess(createdUserId);
                } else {
                    callback.onError("El nombre de usuario ya existe.");
                }
            } catch (Exception e) {
                callback.onError("No se pudo completar el registro. Intentalo de nuevo.");
            }
        });
    }

    public LiveData<List<Task>> getTasksLiveData(int userId) {
        return taskDao.getTasksByUserIdLiveData(userId);
    }

    public void insertTask(Task task) {
        executor.execute(() -> taskDao.insertTask(task));
    }

    public void deleteTask(Task task) {
        executor.execute(() -> taskDao.deleteTask(task));
    }

    public void updateTask(Task task) {
        executor.execute(() -> taskDao.updateTask(task));
    }

    public void completeTask(int taskId, int userId, TaskCompleteCallback callback) {
        executor.execute(() -> {
            try {
                final int[] rewardXP = {0};
                final int[] rewardBerries = {0};
                final int[] ecoReward = {0};
                final boolean[] leveledUp = {false};

                Boolean result = db.runInTransaction(() -> {
                    Task task = taskDao.getTaskById(taskId);
                    if (task == null || TaskRecurrenceUtils.isCompletedForCurrentPeriod(task)) {
                        return false;
                    }

                    User user = userDao.getUserById(userId);
                    if (user == null) {
                        return false;
                    }

                    rewardXP[0] = task.getRewardXP();
                    rewardBerries[0] = task.getRewardBerries();
                    ecoReward[0] = task.getEcoReward();

                    leveledUp[0] = user.addExperience(rewardXP[0]);
                    user.addBerries(rewardBerries[0]);
                    user.addEcoCoins(ecoReward[0]);
                    task.setLastCompletedAt(System.currentTimeMillis());
                    task.setCompleted(!task.isRecurring());

                    userDao.updateUser(user);
                    taskDao.updateTask(task);

                    return true;
                });

                if (result != null && result) {
                    if (callback != null) {
                        callback.onSuccess(rewardXP[0], rewardBerries[0], ecoReward[0], leveledUp[0]);
                    }
                } else if (callback != null) {
                    callback.onError("La tarea ya estaba completada para este periodo o no existe");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Error al completar la tarea: " + e.getMessage());
                }
            }
        });
    }

    public LiveData<List<Furniture>> getShopCatalog() {
        return furnitureDao.getAllFurnitureLiveData();
    }

    public LiveData<List<Furniture>> getUserInventory(int userId) {
        return furnitureDao.getInventoryForUserLiveData(userId);
    }

    public void purchaseFurniture(int userId, Furniture furniture, PurchaseCallback callback) {
        executor.execute(() -> {
            try {
                Boolean result = db.runInTransaction(() -> {
                    if (furnitureDao.countUserFurniture(userId, furniture.getId()) > 0) {
                        return false;
                    }

                    User user = userDao.getUserById(userId);
                    if (user == null) {
                        return false;
                    }

                    if (user.getBerries() < furniture.getPrice()) {
                        return false;
                    }

                    user.setBerries(user.getBerries() - furniture.getPrice());
                    userDao.updateUser(user);
                    userDao.insertUserFurnitureCrossRef(new UserFurnitureCrossRef(userId, furniture.getId()));
                    return true;
                });

                if (result != null && result) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else if (callback != null) {
                    callback.onError("No se pudo realizar la compra (saldo insuficiente o ya posees el objeto)");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Error en la base de datos: " + e.getMessage());
                }
            }
        });
    }

    private void seedStarterTasks(int userId) {
        taskDao.insertTask(new Task(userId, "Beber agua", "Empieza el dia cuidandote", Task.CATEGORY_HEALTH, 10, 5));
        taskDao.insertTask(new Task(userId, "Planificar el dia", "Anota tus 3 prioridades", Task.CATEGORY_GENERAL, 15, 8));
        taskDao.insertTask(new Task(userId, "Mover el cuerpo", "Da un paseo corto o estira", Task.CATEGORY_HEALTH, 20, 10));
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface BooleanCallback {
        void onResult(boolean result);
    }

    public interface RegistrationCallback {
        void onSuccess(int userId);
        void onError(String message);
    }

    public interface PurchaseCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface TaskCompleteCallback {
        void onSuccess(int rewardXP, int rewardBerries, int ecoReward, boolean leveledUp);
        void onError(String message);
    }
}
