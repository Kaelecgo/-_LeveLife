package com.irenaprokhyra.levelife.model;

import android.app.Application;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainRepository {
    private final UserDao userDao;
    private final TaskDao taskDao;
    private final FurnitureDao furnitureDao;
    // Executor para ejecutar tareas en segundo plano (PSP)
    private final ExecutorService executorService;

    public MainRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        taskDao = db.taskDao();
        furnitureDao = db.furnitureDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // --- MÉTODOS MODULARES PARA USUARIO ---
    public void insertUser(User user) {
        executorService.execute(() -> userDao.insertUser(user));
    }
    // Nota: Este metodo se usará con precaución más adelante
    public User getUserSync() { return userDao.getUser(); }

    // --- MÉTODOS MODULARES PARA TAREAS ---
    public void insertTask(Task task) {
        executorService.execute(() -> taskDao.insertTask(task));
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDao.updateTask(task));
    }

    // Los métodos que devuelven listas los manejaremos con LiveData o hilos más adelante

    // --- MÉTODOS PARA MUEBLES ---
    public void insertFurniture(Furniture furniture) {
        executorService.execute(() -> furnitureDao.insertFurniture(furniture));
    }
    // Nota: Las consultas de listas se manejan de forma distinta,
    // lo veremos en la fase de la Interfaz.
}
