package com.irenaprokhyra.levelife.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.irenaprokhyra.levelife.util.PasswordUtils;
import com.irenaprokhyra.levelife.util.RoomPlacementRules;
import com.irenaprokhyra.levelife.util.TaskRecurrenceUtils;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.LongSupplier;

public class MainRepository {
    private static final int WELCOME_BERRIES = 50;
    private static volatile MainRepository instance;

    private final AppDatabase db;
    private final UserDao userDao;
    private final TaskDao taskDao;
    private final FurnitureDao furnitureDao;
    private final TaskCompletionDao taskCompletionDao;
    private final PlacedFurnitureDao placedFurnitureDao;
    private final UserFrequencyHintDao userFrequencyHintDao;
    private final Executor executor;
    private final LongSupplier nowProvider;

    private MainRepository(Application application) {
        this(AppDatabase.getInstance(application),
             AppDatabase.databaseWriteExecutor,
             System::currentTimeMillis);
    }

    MainRepository(AppDatabase db, Executor executor, LongSupplier nowProvider) {
        this.db = db;
        this.userDao = db.userDao();
        this.taskDao = db.taskDao();
        this.furnitureDao = db.furnitureDao();
        this.taskCompletionDao = db.taskCompletionDao();
        this.placedFurnitureDao = db.placedFurnitureDao();
        this.userFrequencyHintDao = db.userFrequencyHintDao();
        this.executor = executor;
        this.nowProvider = nowProvider;
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

    public void markFrequencyHintSeenIfNeeded(int userId, String frequency, BooleanCallback callback) {
        executor.execute(() -> {
            String normalizedFrequency = Task.normalizeFrequency(frequency);
            boolean updated = userFrequencyHintDao.insert(
                    new UserFrequencyHint(userId, normalizedFrequency)
            ) != -1L;
            if (callback != null) {
                callback.onResult(updated);
            }
        });
    }

    public void resetFrequencyHints(int userId) {
        executor.execute(() -> userFrequencyHintDao.deleteAllForUser(userId));
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
                    newUser.setStarterTaskPackVersion(User.CURRENT_STARTER_TASK_PACK_VERSION);

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
                final int[] rewards = {0, 0, 0}; // XP, Berries, Eco
                final boolean[] leveledUp = {false};

                Boolean result = db.runInTransaction(() -> {
                    long now = nowProvider.getAsLong();
                    Task task = taskDao.getTaskById(taskId);

                    if (task == null || task.getUserId() != userId) return false;

                    // 1. VALIDACIÓN: Usamos el historial real de la DB
                    if (task.isRecurring()) {
                        long periodStart = TaskRecurrenceUtils.getCurrentPeriodStart(task.getFrequency(), now);
                        if (taskCompletionDao.hasCompletionSince(taskId, periodStart)) {
                            return false; // Ya se completó en este periodo (día/semana/mes)
                        }
                    } else if (task.isCompleted()) {
                        return false; // Tarea única ya terminada
                    }

                    User user = userDao.getUserById(userId);
                    if (user == null) return false;

                    // 2. RECOMPENSAS
                    rewards[0] = task.getRewardXP();
                    rewards[1] = task.getRewardBerries();
                    rewards[2] = task.getEcoReward();

                    leveledUp[0] = user.addExperience(rewards[0]);
                    user.addBerries(rewards[1]);
                    user.addEcoCoins(rewards[2]);

                    // 3. PERSISTENCIA
                    userDao.updateUser(user);

                    // Insertamos registro en el historial
                    taskCompletionDao.insertCompletion(new TaskCompletion(taskId, userId, now));

                    // Actualizamos la "caché" en la tarea para la UI
                    task.setLastCompletedAt(now);
                    if (!task.isRecurring()) {
                        task.setCompleted(true);
                    }
                    taskDao.updateTask(task);

                    return true;
                });

                if (result != null && result) {
                    if (callback != null) {
                        callback.onSuccess(rewards[0], rewards[1], rewards[2], leveledUp[0]);
                    }
                } else if (callback != null) {
                    callback.onError("La tarea ya está completada o no tienes permiso");
                }
            } catch (Exception e) {
                if (callback != null) callback.onError("Error: " + e.getMessage());
            }
        });
    }

    public LiveData<List<Furniture>> getShopCatalog() {
        return furnitureDao.getAllFurnitureLiveData();
    }

    public LiveData<List<Furniture>> getUserInventory(int userId) {
        return furnitureDao.getInventoryForUserLiveData(userId);
    }

    public LiveData<List<PlacedFurnitureItem>> getPlacedFurnitureItemsForUserLiveData(int userId) {
        return placedFurnitureDao.getPlacedFurnitureItemsForUserLiveData(userId);
    }

    public void placeFurniture(int userId, Furniture furniture, String slot, PlacementCallback callback) {
        executor.execute(() -> {
            try {
                long now = nowProvider.getAsLong();
                Boolean result = db.runInTransaction(() -> placeFurnitureInTransaction(userId, furniture, slot, now));

                if (result != null && result) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else if (callback != null) {
                    callback.onError("No se pudo colocar el mueble");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Error en la base de datos: " + e.getMessage());
                }
            }
        });
    }

    private boolean placeFurnitureInTransaction(int userId, Furniture furniture, String slot, long now) {
        if (furniture == null || !PlacedFurniture.isValidSlot(slot)) {
            return false;
        }

        User user = userDao.getUserById(userId);
        if (user == null) {
            return false;
        }

        if (furnitureDao.countUserFurniture(userId, furniture.getId()) <= 0) {
            return false;
        }

        String canonicalTargetSlot = RoomPlacementRules.normalizeStoredSlot(slot, furniture);
        List<PlacedFurniture> placedFurnitureForUser = placedFurnitureDao.getPlacedFurnitureForUser(userId);
        PlacedFurniture existingInTargetSlot = null;
        PlacedFurniture existingForFurniture = null;
        String existingFurnitureVisualSlot = null;

        for (PlacedFurniture placedFurniture : placedFurnitureForUser) {
            Furniture placedFurnitureModel = furnitureDao.getFurnitureById(placedFurniture.getFurnitureId());
            String visualSlot = RoomPlacementRules.normalizeStoredSlot(
                    placedFurniture.getSlot(),
                    placedFurnitureModel
            );

            if (placedFurniture.getFurnitureId() == furniture.getId()) {
                existingForFurniture = placedFurniture;
                existingFurnitureVisualSlot = visualSlot;
                continue;
            }

            if (canonicalTargetSlot.equals(visualSlot)) {
                existingInTargetSlot = placedFurniture;
            }
        }

        if (existingForFurniture != null) {
            if (canonicalTargetSlot.equals(existingFurnitureVisualSlot)) {
                existingForFurniture.setSlot(canonicalTargetSlot);
                existingForFurniture.setPlacedAt(now);
                placedFurnitureDao.updatePlacedFurniture(existingForFurniture);
                return true;
            }

            if (existingInTargetSlot != null && existingInTargetSlot.getId() != existingForFurniture.getId()) {
                placedFurnitureDao.removePlacedFurnitureById(existingInTargetSlot.getId());
            }

            existingForFurniture.setSlot(canonicalTargetSlot);
            existingForFurniture.setPlacedAt(now);
            placedFurnitureDao.updatePlacedFurniture(existingForFurniture);
            return true;
        }

        if (existingInTargetSlot != null) {
            placedFurnitureDao.removePlacedFurnitureById(existingInTargetSlot.getId());
        }

        PlacedFurniture placedFurniture = new PlacedFurniture(
                userId,
                furniture.getId(),
                canonicalTargetSlot,
                now
        );
        placedFurnitureDao.insertPlacedFurniture(placedFurniture);
        return true;
    }

    public void removePlacedFurniture(int userId, String slot, PlacementCallback callback) {
        executor.execute(() -> {
            try {
                Boolean result = db.runInTransaction(() -> {
                    if (!PlacedFurniture.isValidSlot(slot)) { return false; }

                    User user = userDao.getUserById(userId);
                    if (user == null) { return false; }

                    placedFurnitureDao.removePlacedFurnitureForSlot(userId, slot);
                    return true;
                });
                if (result != null && result) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else if (callback != null) {
                    callback.onError("No se pudo retirar el mueble");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Error en la base de datos: " + e.getMessage());
                }
            }
        });
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

                    boolean purchaseSucceeded = furniture.isEcoCurrency()
                            ? user.spendEcoCoins(furniture.getPrice())
                            : user.spendBerries(furniture.getPrice());

                    if (!purchaseSucceeded) {
                        return false;
                    }

                    userDao.updateUser(user);
                    userDao.insertUserFurnitureCrossRef(new UserFurnitureCrossRef(userId, furniture.getId()));
                    return true;
                });

                if (result != null && result) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else if (callback != null) {
                    callback.onError("No se pudo realizar la compra con la moneda disponible o ya posees el objeto");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Error en la base de datos: " + e.getMessage());
                }
            }
        });
    }

    private void seedStarterTasks(int userId) {
        taskDao.insertTask(new Task(
                userId,
                "Apagar luces innecesarias",
                "Haz una pequena accion diaria para reducir tu consumo energetico",
                Task.CATEGORY_ECO,
                10, 5, 1,
                Task.DIFFICULTY_EASY,
                Task.FREQUENCY_DAILY,
                true
        ));

        taskDao.insertTask(new Task(
                userId,
                "Beber agua",
                "Empieza el dia cuidandote",
                Task.CATEGORY_HEALTH,
                10, 5, 0,
                Task.DIFFICULTY_EASY,
                Task.FREQUENCY_DAILY,
                false
        ));

        taskDao.insertTask(new Task(
                userId,
                "Ordenar tu escritorio",
                "Dedica unos minutos a dejar limpia tu zona de trabajo",
                Task.CATEGORY_GENERAL,
                15, 8, 0,
                Task.DIFFICULTY_EASY,
                Task.FREQUENCY_ONCE,
                false
        ));

        taskDao.insertTask(new Task(
                userId,
                "Caminar 30 minutos",
                "Haz una caminata larga para activar el cuerpo",
                Task.CATEGORY_HEALTH,
                20, 10, 0,
                Task.DIFFICULTY_MEDIUM,
                Task.FREQUENCY_WEEKLY,
                false
        ));

        taskDao.insertTask(new Task(
                userId,
                "Revisar tu consumo de energia",
                "Busca un pequeno cambio para ahorrar luz o calefaccion este mes",
                Task.CATEGORY_ECO,
                40, 20, 3,
                Task.DIFFICULTY_HARD,
                Task.FREQUENCY_MONTHLY,
                true
        ));
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

    public interface PlacementCallback {
        void onSuccess();
        void onError(String message);
    }
}
