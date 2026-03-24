package com.irenaprokhyra.levelife.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MainRepository {
    private static volatile MainRepository instance;
    private final UserDao userDao;
    private final TaskDao taskDao;
    private final FurnitureDao furnitureDao;
    private final ExecutorService executor;

    private MainRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        taskDao = db.taskDao();
        furnitureDao = db.furnitureDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public static synchronized MainRepository getInstance(Application application) {
        if (instance == null) instance = new MainRepository(application);
        return instance;
    }

    // --- SECCIÓN USUARIO ---
    public LiveData<User> getUserLiveData(int userId) {
        return userDao.getUserByIdLiveData(userId);
    }

    public void loginUser(String username, String password, LoginCallback callback) {
        executor.execute(() -> {
            User user = userDao.login(username, password);
            if (user != null) callback.onSuccess(user);
            else callback.onError("Usuario o contraseña incorrectos");
        });
    }

    public void getUserById(int userId, LoginCallback callback) {
        executor.execute(() -> {
            User user = userDao.getUserById(userId);
            if (user != null) callback.onSuccess(user);
            else callback.onError("Usuario no encontrado");
        });
    }

    public void checkUserExists(String username, BooleanCallback callback) {
        executor.execute(() -> {
            int count = userDao.checkUserExists(username);
            callback.onResult(count > 0);
        });
    }

    public void insertUser(User user) {
        executor.execute(() -> userDao.insertUser(user));
    }

    public void updateUser(User user) {
        executor.execute(() -> userDao.updateUser(user));
    }

    // --- SECCIÓN TAREAS ---
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

    // --- SECCIÓN TIENDA E INVENTARIO ---
    public LiveData<List<Furniture>> getShopCatalog() {
        return furnitureDao.getAllFurnitureLiveData();
    }

    public LiveData<List<Furniture>> getUserInventory(int userId) {
        return furnitureDao.getInventoryForUserLiveData(userId);
    }

    public void purchaseFurniture(User user, int furnitureId, Runnable onSuccess) {
        executor.execute(() -> {
            try {
                AppDatabase.getInstance(null).runInTransaction(() -> {
                    userDao.updateUser(user);
                    userDao.insertUserFurnitureCrossRef(new UserFurnitureCrossRef(user.getId(), furnitureId));
                });
                if (onSuccess != null) onSuccess.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface BooleanCallback {
        void onResult(boolean result);
    }
}