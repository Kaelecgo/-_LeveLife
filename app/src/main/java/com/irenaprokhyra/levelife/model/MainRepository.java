package com.irenaprokhyra.levelife.model;

import android.app.Application;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainRepository {
    // Instancia estática para el Singleton
    private static MainRepository instance;

    private final UserDao userDao;
    private final TaskDao taskDao;
    private final FurnitureDao furnitureDao;
    // Executor para ejecutar tareas en segundo plano (PSP)
    private final ExecutorService executorService;

    private MainRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        taskDao = db.taskDao();
        furnitureDao = db.furnitureDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Metodo Singleton: Para obtener el repositorio desde la Activity
    public static synchronized MainRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MainRepository(application);
        }
        return instance;
    }

    // Interfaz Callback para comunicar resultados a la Activity
    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface TaskListCallback {
        void onSuccess(List<Task> tasks);
        void onError(String message);
    }

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
                if (user != null) {
                    callback.onSuccess(user);
                } else {
                    callback.onError("No se encuentra usuario.");
                }
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

    // Nota: Este metodo se usará con precaución más adelante
    public User getUserSync(int id) { return userDao.getUserById(id); }

    // >_  MÉTODOS PARA TAREAS _<
    public void insertTask(Task task) {
        executorService.execute(() -> taskDao.insertTask(task));
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDao.updateTask(task));
    }
    // >_ METODO DE ELIMINACIÓN _<
    public void deleteTask(Task task) {
        executorService.execute(() -> taskDao.deleteTask(task));
    }

    public void getTaskForUser(int userId, TaskListCallback callback) {
        executorService.execute(() -> {
            try {
                List<Task> tasks = taskDao.getTasksByUserId(userId);
                callback.onSuccess(tasks);
            } catch (Exception e) {
                callback.onError("Error al cargar las tareas: " + e.getMessage());
            }
        });
    }


    // Los métodos que devuelven listas los manejaremos con LiveData o hilos más adelante

    // >_  MÉTODOS PARA MUEBLES _<
    public void insertFurniture(Furniture furniture) {
        executorService.execute(() -> furnitureDao.insertFurniture(furniture));
    }
    // Nota: Las consultas de listas se manejan de forma distinta,
    // lo veremos en la fase de la Interfaz.
}
