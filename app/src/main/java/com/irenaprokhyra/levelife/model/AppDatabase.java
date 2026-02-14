package com.irenaprokhyra.levelife.model;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Task.class, Furniture.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract TaskDao taskDao();
    public abstract FurnitureDao furnitureDao();

    // Patrón Singleton para evitar abrir varias instancias de la base de datos
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // Executor para operaciones de base de datos (Global para la app)
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "levelife_db")
                            // Estrategia destructiva: Si cambias la versión, borra todo y empieza de cero
                            // (Útil en desarrollo para no lidiar con migraciones complejas aún)
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Callback: Se ejecuta SOLO cuando se crea la base de datos por primera vez
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Ejecutamos en segundo plano la inserción del usuario Admin
            databaseWriteExecutor.execute(() -> {
                UserDao userDao = INSTANCE.userDao();
                // Creamos el Usuario Admin por defecto
                User admin = new User("admin", "1234");
                admin.setLevel(1);
                admin.setExperience(0);
                admin.setBerries(100);
                userDao.insertUser(admin);

                // >_ INSERCIÓN DE TAREAS _<
                TaskDao taskDao = INSTANCE.taskDao();
                // Tarea 1: Facil
                Task t1 = new Task();
                t1.setTitle("Beber agua");
                t1.setDescription("Hidrátate con un vaso de agua");
                t1.setCategory("Salud");
                t1.setFrequency("Diaria");
                t1.setRewardXP(10);
                t1.setRewardBerries(5);
                t1.setCompleted(false);
                t1.setUserId(1);
                taskDao.insertTask(t1);

                Task t2 = new Task();
                t2.setTitle("Estudiar Android");
                t2.setDescription("Completar el módulo de Room Database");
                t2.setCategory("Estudios");
                t2.setFrequency("Única");
                t2.setRewardXP(50);
                t2.setRewardBerries(20);
                t2.setCompleted(false);
                t2.setUserId(1);
                taskDao.insertTask(t2);
            });
        }
    };
}