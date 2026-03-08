package com.irenaprokhyra.levelife.model;

import android.app.Application;
import java.util.List;
import java.util.concurrent.ExecutorService;


public class MainRepository {
    // Instancia estática para el Singleton
    private static volatile MainRepository instance;

    private final UserDao userDao;
    private final TaskDao taskDao;
    private final FurnitureDao furnitureDao;
    // >_ REUTILIZAR EL EXECUTOR DE LA BBDD _<
    // No creamos uno nuevo, usamos el que ya gestiona AppDatabase
    private final ExecutorService executorService;

    private MainRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        taskDao = db.taskDao();
        furnitureDao = db.furnitureDao();
        // Usamos el mismo hilo de ejecución que la base de datos
        executorService = AppDatabase.databaseWriteExecutor;
    }

    // Metodo Singleton para obtener el repositorio desde la Activity
    public static synchronized MainRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MainRepository(application);
        }
        return instance;
    }

    // >_ INTERFACES CALLBACK (Para comunicar resultados a la Activity) _<
    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface TaskListCallback {
        void onSuccess(List<Task> tasks);
        void onError(String message);
    }
    // >_ Callback para la Tienda _<
    public interface FurnitureListCallback {
        void onSuccess(List<Furniture> furnitureList);
        void onError(String message);
    }

    public interface FurnitureCallback {
        void onSuccess(Furniture furniture);
        void onError(String message);
    }

    // >_ Callback genérico para booleanos _<
    public interface BooleanCallback {
        void onResult(boolean exists);
    }

    // -------------------------------------
    // >_ SECCIÓN DE USUARIOS (User) _<
    // -------------------------------------

    // Lógica de Login en segundo plano
    public void loginUser(String username, String password, LoginCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.login(username, password);
                if (user != null) callback.onSuccess(user);
                else callback.onError("Login failed: User not found");
            } catch (Exception e) {
                callback.onError("Login failed: " + e.getMessage());
            }
        });
    }

    // >_ METODO PARA OBTENER USUARIO _<
    public void getUserById(int userId, LoginCallback callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserById(userId);
                if (user != null) callback.onSuccess(user);
                else callback.onError("No se encuentra usuario.");
            } catch (Exception e) {
                callback.onError("Error al cargar el perfil de usuario: " + e.getMessage());
            }
        });
    }

    // >_ MÉTODOS PARA USUARIO _<
    public void insertUser(User user) {
        executorService.execute(() -> userDao.insertUser(user));
    }
    // >_ METODO PARA ACTUALIZACIÓN _<
    public void updateUser(User user) {
        executorService.execute(() -> userDao.updateUser(user));
    }
    // >_ METODO DE VALIDACIÓN DE REGISTRO _<
    public void checkUserExists(String username, BooleanCallback callback) {
        executorService.execute(() -> {
            int count = userDao.checkUserExists(username);
            callback.onResult(count > 0);
        });
    }
    // Nota: Este metodo se usará con precaución más adelante
    public User getUserSync(int id) { return userDao.getUserById(id); }

    // -------------------------------------
    // >_ SECCIÓN DE TAREAS (Task) _<
    // -------------------------------------

    // >_  MÉTODOS PARA TAREAS _<
    public void insertTask(Task task, Runnable onComplete) {
        executorService.execute(() ->  {
            taskDao.insertTask(task);
            if (onComplete != null) onComplete.run(); // Avisamos cuando se haya guardado físicamente
        });
    }

    // >_ FIX: Añadimos un Runnable para avisar cuando la escritura termine _<
    public void updateTask(Task task, Runnable onComplete) {
        executorService.execute(() -> {
            taskDao.updateTask(task);
            if (onComplete != null) onComplete.run(); // Avisamos a la Activity de que ya hemos guardado
        });
    }
    // >_ METODO DE ELIMINACIÓN _<
    public void deleteTask(Task task, Runnable onComplete) {
        executorService.execute(() -> {
            taskDao.deleteTask(task);
            if (onComplete != null) onComplete.run(); // Avisamos cuando se haya borrado físicamente
        });
    }

    public void getTaskForUser(int userId, TaskListCallback callback) {
        executorService.execute(() -> {
            try {
                // Ahora TaskDao devuelve las tareas ordenadas (Pendientes primero)
                List<Task> tasks = taskDao.getTasksByUserId(userId);
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError("Error al cargar las tareas: " + e.getMessage());
            }
        });
    }
    // -------------------------------------
    // >_ SECCIÓN DE TIENDA (Furniture) _<
    // -------------------------------------

    // >_  MÉTODOS PARA MUEBLES _<
    public void insertFurniture(Furniture furniture) {
        executorService.execute(() -> furnitureDao.insertFurniture(furniture));
    }

    // Recuperar el catálogo entero
    public void getAllFurniture(FurnitureListCallback callback) {
        executorService.execute(() -> {
            try {
                List<Furniture> list = furnitureDao.getAllFurniture();
                callback.onSuccess(list);
            } catch (Exception e) {
                callback.onError("Error cargando tienda: " + e.getMessage());
            }
        });
    }
    // Recuperar un mueble específico (para comprarlo)
    public void getFurnitureById(int id, FurnitureCallback callback) {
        executorService.execute(() -> {
            try {
                Furniture furniture = furnitureDao.getFurnitureById(id);
                if (furniture != null) callback.onSuccess(furniture);
                else callback.onError("No se encuentra mueble.");
            } catch (Exception e) {
                callback.onError("Error al cargar el mueble: " + e.getMessage());
            }
        });
    }

    // ------------------------------------------
    // >_ SECCIÓN DE INVENTARIO (Relación N:M) _<
    // ------------------------------------------

    public void buyFurniture(int userId, int furnitureId) {
        executorService.execute(() -> {
            UserFurnitureCrossRef purchaseRecord = new UserFurnitureCrossRef(userId, furnitureId);
            userDao.insertUserFurnitureCrossRef(purchaseRecord);
        });
    }
}