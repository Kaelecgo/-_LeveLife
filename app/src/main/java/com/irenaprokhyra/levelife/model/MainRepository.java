package com.irenaprokhyra.levelife.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MainRepository {
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

    /**
     * Completa una tarea de forma atómica, otorgando recompensas al usuario.
     */
    public void completeTask(int taskId, int userId, TaskCompleteCallback callback) {
        executor.execute(() -> {
            try {
                Boolean result = db.runInTransaction(() -> {
                    // 1. Obtener la tarea y validar
                    Task task = taskDao.getTaskById(taskId);
                    if (task == null || task.isCompleted()) {
                        return false;
                    }

                    // 2. Obtener el usuario
                    User user = userDao.getUserById(userId);
                    if (user == null) {
                        return false;
                    }

                    // 3. Aplicar recompensas y marcar como completada
                    user.addExperience(task.getRewardXP());
                    user.addBerries(task.getRewardBerries());
                    task.setCompleted(true);

                    // 4. Guardar cambios
                    userDao.updateUser(user);
                    taskDao.updateTask(task);

                    return true;
                });

                if (result != null && result) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onError("La tarea ya estaba completada o no existe");
                }
            } catch (Exception e) {
                if (callback != null) callback.onError("Error al completar la tarea: " + e.getMessage());
            }
        });
    }

    // --- SECCIÓN TIENDA E INVENTARIO ---
    public LiveData<List<Furniture>> getShopCatalog() {
        return furnitureDao.getAllFurnitureLiveData();
    }

    public LiveData<List<Furniture>> getUserInventory(int userId) {
        return furnitureDao.getInventoryForUserLiveData(userId);
    }

    /**
     * Realiza la compra de un mueble de forma atómica.
     * Valida existencia previa y saldo dentro de una transacción.
     */
    public void purchaseFurniture(int userId, Furniture furniture, PurchaseCallback callback) {
        executor.execute(() -> {
            try {
                // Usamos runInTransaction con un Callable para retornar el resultado de la operación
                Boolean result = db.runInTransaction(() -> {
                    // 1. Comprobar si el usuario ya posee el mueble
                    if (furnitureDao.countUserFurniture(userId, furniture.getId()) > 0) {
                        return false; 
                    }

                    // 2. Obtener datos frescos del usuario para validar saldo
                    User user = userDao.getUserById(userId);
                    if (user == null) return false;

                    // 3. Comprobar si tiene saldo suficiente
                    if (user.getBerries() < furniture.getPrice()) {
                        return false;
                    }

                    // 4. Restar bayas y guardar la relación
                    user.setBerries(user.getBerries() - furniture.getPrice());
                    userDao.updateUser(user);
                    userDao.insertUserFurnitureCrossRef(new UserFurnitureCrossRef(userId, furniture.getId()));
                    
                    return true;
                });

                if (result != null && result) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onError("No se pudo realizar la compra (saldo insuficiente o ya posees el objeto)");
                }
            } catch (Exception e) {
                if (callback != null) callback.onError("Error en la base de datos: " + e.getMessage());
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

    public interface PurchaseCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface TaskCompleteCallback {
        void onSuccess();
        void onError(String message);
    }
}
