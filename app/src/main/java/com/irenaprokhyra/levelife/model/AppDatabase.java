package com.irenaprokhyra.levelife.model;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// >_ IMPORTANTE: Subimos la versión a 2 para forzar la recreación con los nuevos índices _<
@Database(entities = {User.class, Task.class, Furniture.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract TaskDao taskDao();
    public abstract FurnitureDao furnitureDao();

    // Patrón Singleton para evitar abrir varias instancias de la base de datos
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // Executor para operaciones en segundo plano
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "levelife_db")
                            // Estrategia destructiva: Borra y crea de nuevo si cambia la versión
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // >_ CALLBACK DE POBLADO (SEEDER) _<
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Ejecutamos en segundo plano la inserción del usuario Admin
            databaseWriteExecutor.execute(() -> {
                // Limpieza inicial
                UserDao userDao = INSTANCE.userDao();
                TaskDao taskDao = INSTANCE.taskDao();
                FurnitureDao furnitureDao = INSTANCE.furnitureDao();

                // CREAR USUARIOS (constructor nuevo)
                User admin = new User("admin", "1234");
                admin.setBerries(1000); // El admin empieza rico para pruebas
                userDao.insertUser(admin);

                // >_ INSERCIÓN DE USUARIO PRUEBA _<
                User irena = new User("irena", "1234");
                irena.setBerries(50);
                userDao.insertUser(irena);

                // CREAR TAREAS
                // Asumimos que admin es ID 1 e irena es ID 2
                taskDao.insertTask(new Task(1, "Beber agua", "Hidrátate", "Salud", 10, 5));
                taskDao.insertTask(new Task(1, "Estudiar Android", "Room Database", "Estudios", 50, 20));

                // Tareas para Irena
                taskDao.insertTask(new Task(2, "Hacer la cama", "Antes de salir", "Hogar", 15, 10));

                // CREAR MUEBLES (PREPARACIÓN HITO 4 - TIENDA)
                furnitureDao.insertFurniture(new Furniture("Silla Madera", 50, "Básico", "furn_chair_wood"));
                furnitureDao.insertFurniture(new Furniture("Planta", 30, "Decoración", "furn_plant_small"));
                furnitureDao.insertFurniture(new Furniture("PC Gamer", 500, "Tecnología", "furn_pc_gamer"));
                furnitureDao.insertFurniture(new Furniture("Lámpara", 80, "Iluminación", "furn_lamp_desk"));

            });
        }
    };
}