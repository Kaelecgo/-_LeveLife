package com.irenaprokhyra.levelife.model;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                User.class,
                Task.class,
                Furniture.class,
                UserFurnitureCrossRef.class,
                TaskCompletion.class
        },
        version = 8,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;

    private static volatile AppDatabase INSTANCE;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_tasks_user_id` ON `tasks` (`user_id`)"
            );
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `user_furniture_cross_ref` (" +
                            "`userId` INTEGER NOT NULL, " +
                            "`furnitureId` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`userId`, `furnitureId`), " +
                            "FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`furnitureId`) REFERENCES `furniture`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
            );
            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_user_furniture_cross_ref_furnitureId` " +
                            "ON `user_furniture_cross_ref` (`furnitureId`)"
            );
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `furniture` ADD COLUMN `description` TEXT");
            database.execSQL("ALTER TABLE `furniture` ADD COLUMN `type` TEXT");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // La version 5 solo introdujo datos de ejemplo, no cambios de esquema.
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            normalizeDuplicateUsernames(database);
            database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_user_name` " +
                            "ON `users` (`user_name`)"
            );
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `tasks` ADD COLUMN `eco_reward` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE `tasks` ADD COLUMN `is_eco_task` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE `tasks` ADD COLUMN `last_completed_at` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE `tasks` ADD COLUMN `difficulty` TEXT");
            database.execSQL("ALTER TABLE `users` ADD COLUMN `eco_coins` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `task_completions` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`task_id` INTEGER NOT NULL, " +
                            "`user_id` INTEGER NOT NULL, " +
                            "`completed_at` INTEGER NOT NULL, " +
                            "FOREIGN KEY(`task_id`) REFERENCES `tasks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                            ")"
            );

            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_task_completions_user_id` " +
                            "ON `task_completions` (`user_id`)"
            );

            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_task_completions_task_id_completed_at` " +
                            "ON `task_completions` (`task_id`, `completed_at`)"
            );
        }
    };

    public abstract UserDao userDao();

    public abstract TaskDao taskDao();

    public abstract FurnitureDao furnitureDao();

    public abstract TaskCompletionDao taskCompletionDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                                    AppDatabase.class,
                                    "levelife_db"
                            )
                            .fallbackToDestructiveMigrationOnDowngrade()
                            .addMigrations(
                                    MIGRATION_1_2,
                                    MIGRATION_2_3,
                                    MIGRATION_3_4,
                                    MIGRATION_4_5,
                                    MIGRATION_5_6,
                                    MIGRATION_6_7,
                                    MIGRATION_7_8
                            )
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void normalizeDuplicateUsernames(@NonNull SupportSQLiteDatabase database) {
        Set<String> usedNames = new HashSet<>();
        Cursor cursor = database.query("SELECT id, user_name FROM users ORDER BY id ASC");

        try {
            int idColumn = cursor.getColumnIndex("id");
            int usernameColumn = cursor.getColumnIndex("user_name");

            while (cursor.moveToNext()) {
                int userId = cursor.getInt(idColumn);
                String username = cursor.getString(usernameColumn);

                if (username == null || username.trim().isEmpty()) {
                    username = "user_" + userId;
                    database.execSQL(
                            "UPDATE users SET user_name = ? WHERE id = ?",
                            new Object[]{username, userId}
                    );
                }

                if (usedNames.contains(username)) {
                    String uniqueUsername = buildUniqueUsername(usedNames, username, userId);
                    database.execSQL(
                            "UPDATE users SET user_name = ? WHERE id = ?",
                            new Object[]{uniqueUsername, userId}
                    );
                    username = uniqueUsername;
                }

                usedNames.add(username);
            }
        } finally {
            cursor.close();
        }
    }

    private static String buildUniqueUsername(Set<String> usedNames, String username, int userId) {
        String baseName = username + "_" + userId;
        String candidate = baseName;
        int suffix = 1;

        while (usedNames.contains(candidate)) {
            candidate = baseName + "_" + suffix;
            suffix++;
        }

        return candidate;
    }

    private static void seedFurnitureCatalog(SupportSQLiteDatabase db) {
        db.execSQL("INSERT INTO furniture (name, price, category, image_ref, description, type) VALUES ('Silla Madera', 50, 'Basico', 'furn_chair_wood', NULL, NULL)");
        db.execSQL("INSERT INTO furniture (name, price, category, image_ref, description, type) VALUES ('Planta', 30, 'Decoracion', 'furn_plant_small', NULL, NULL)");
        db.execSQL("INSERT INTO furniture (name, price, category, image_ref, description, type) VALUES ('PC Gamer', 500, 'Tecnologia', 'furn_pc_gamer', NULL, NULL)");
        db.execSQL("INSERT INTO furniture (name, price, category, image_ref, description, type) VALUES ('Lampara', 80, 'Iluminacion', 'furn_lamp_desk', NULL, NULL)");
        db.execSQL("INSERT INTO furniture (name, price, category, image_ref, description, type) VALUES ('Estanteria', 120, 'Almacenaje', 'furn_shelf', NULL, NULL)");
        db.execSQL("INSERT INTO furniture (name, price, category, image_ref, description, type) VALUES ('Cama Comoda', 300, 'Descanso', 'furn_bed', NULL, NULL)");
        db.execSQL("INSERT INTO furniture (name, price, category, image_ref, description, type) VALUES ('Alfombra', 40, 'Decoracion', 'furn_rug', NULL, NULL)");
        db.execSQL("INSERT INTO furniture (name, price, category, image_ref, description, type) VALUES ('Ventilador Eco', 150, 'Sostenibilidad', 'furn_fan_eco', NULL, NULL)");
    }

    private static void ensureFurnitureCatalogSeeded(SupportSQLiteDatabase db) {
        Cursor cursor = db.query("SELECT COUNT(*) FROM furniture");
        try {
            if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                seedFurnitureCatalog(db);
            }
        } finally {
            cursor.close();
        }
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                ensureFurnitureCatalogSeeded(db);
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                ensureFurnitureCatalogSeeded(db);
            });
        }
    };
}