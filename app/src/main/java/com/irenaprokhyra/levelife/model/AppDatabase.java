package com.irenaprokhyra.levelife.model;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Task.class, Furniture.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract TaskDao taskDao();
    public abstract FurnitureDao furnitureDao();

    // Patrón Singleton para evitar abrir varias instancias de la base de datos
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if(INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "levelife_db").build();
                }
            }
        }
        return INSTANCE;
    }
}


