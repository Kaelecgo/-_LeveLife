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
                TaskCompletion.class,
                PlacedFurniture.class,
                UserFrequencyHint.class
        },
        version = 12,
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

    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `placed_furniture` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`user_id` INTEGER NOT NULL, " +
                            "`furniture_id` INTEGER NOT NULL, " +
                            "`slot` TEXT NOT NULL, " +
                            "`placed_at` INTEGER NOT NULL, " +
                            "FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`furniture_id`) REFERENCES `furniture`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                            ")"
            );

            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_placed_furniture_user_id` " +
                            "ON `placed_furniture` (`user_id`)"
            );

            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_placed_furniture_furniture_id` " +
                            "ON `placed_furniture` (`furniture_id`)"
            );

            database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_placed_furniture_user_id_slot` " +
                            "ON `placed_furniture` (`user_id`, `slot`)"

            );
        }
    };

    private static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            rebuildTasksTable(database);
            rebuildPlacedFurnitureTable(database);
        }
    };

    private static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE `users` ADD COLUMN `seen_frequency_hints_mask` INTEGER NOT NULL DEFAULT 0"
            );
        }
    };

    private static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `user_frequency_hints` (" +
                            "`user_id` INTEGER NOT NULL, " +
                            "`frequency` TEXT NOT NULL, " +
                            "PRIMARY KEY(`user_id`, `frequency`), " +
                            "FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                            ")"
            );
            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_user_frequency_hints_user_id` " +
                            "ON `user_frequency_hints` (`user_id`)"
            );
        }
    };


    public abstract UserDao userDao();

    public abstract TaskDao taskDao();

    public abstract FurnitureDao furnitureDao();

    public abstract TaskCompletionDao taskCompletionDao();

    public abstract PlacedFurnitureDao placedFurnitureDao();

    public abstract UserFrequencyHintDao userFrequencyHintDao();

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
                                    MIGRATION_7_8,
                                    MIGRATION_8_9,
                                    MIGRATION_9_10,
                                    MIGRATION_10_11,
                                    MIGRATION_11_12
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

    private static void rebuildTasksTable(@NonNull SupportSQLiteDatabase database) {
        Set<String> taskColumns = getTableColumns(database, "tasks");
        if (taskColumns.isEmpty()) {
            return;
        }

        database.execSQL("DROP TABLE IF EXISTS `tasks_migrated_v10`");
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS `tasks_migrated_v10` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`user_id` INTEGER NOT NULL, " +
                        "`reward_berries` INTEGER NOT NULL, " +
                        "`reward_xp` INTEGER NOT NULL, " +
                        "`eco_reward` INTEGER NOT NULL DEFAULT 0, " +
                        "`is_eco_task` INTEGER NOT NULL DEFAULT 0, " +
                        "`last_completed_at` INTEGER NOT NULL DEFAULT 0, " +
                        "`title` TEXT, " +
                        "`description` TEXT, " +
                        "`category` TEXT, " +
                        "`difficulty` TEXT, " +
                        "`isCompleted` INTEGER NOT NULL, " +
                        "`frequency` TEXT, " +
                        "FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                        ")"
        );

        String difficultyExpr = taskColumns.contains("difficulty")
                ? "COALESCE(`difficulty`, '" + Task.DIFFICULTY_MEDIUM + "')"
                : "'" + Task.DIFFICULTY_MEDIUM + "'";
        String frequencyExpr = taskColumns.contains("frequency")
                ? "COALESCE(NULLIF(`frequency`, ''), '" + Task.FREQUENCY_ONCE + "')"
                : "'" + Task.FREQUENCY_ONCE + "'";
        String ecoRewardExpr = taskColumns.contains("eco_reward") ? "COALESCE(`eco_reward`, 0)" : "0";
        String isEcoTaskExpr = taskColumns.contains("is_eco_task") ? "COALESCE(`is_eco_task`, 0)" : "0";
        String lastCompletedAtExpr = taskColumns.contains("last_completed_at") ? "COALESCE(`last_completed_at`, 0)" : "0";
        String isCompletedExpr = taskColumns.contains("isCompleted") ? "COALESCE(`isCompleted`, 0)" : "0";

        database.execSQL(
                "INSERT INTO `tasks_migrated_v10` (" +
                        "`id`, `user_id`, `reward_berries`, `reward_xp`, `eco_reward`, `is_eco_task`, `last_completed_at`, " +
                        "`title`, `description`, `category`, `difficulty`, `isCompleted`, `frequency`" +
                        ") " +
                        "SELECT " +
                        "`id`, `user_id`, `reward_berries`, `reward_xp`, " +
                        ecoRewardExpr + ", " +
                        isEcoTaskExpr + ", " +
                        lastCompletedAtExpr + ", " +
                        "`title`, `description`, `category`, " +
                        difficultyExpr + ", " +
                        isCompletedExpr + ", " +
                        frequencyExpr + " " +
                        "FROM `tasks`"
        );

        database.execSQL("DROP TABLE `tasks`");
        database.execSQL("ALTER TABLE `tasks_migrated_v10` RENAME TO `tasks`");
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_user_id` ON `tasks` (`user_id`)");
    }

    private static void rebuildPlacedFurnitureTable(@NonNull SupportSQLiteDatabase database) {
        Set<String> placedFurnitureColumns = getTableColumns(database, "placed_furniture");
        if (placedFurnitureColumns.isEmpty()) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `placed_furniture` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`user_id` INTEGER NOT NULL, " +
                            "`furniture_id` INTEGER NOT NULL, " +
                            "`slot` TEXT NOT NULL, " +
                            "`placed_at` INTEGER NOT NULL, " +
                            "FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`furniture_id`) REFERENCES `furniture`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                            ")"
            );
        } else {
            database.execSQL("DROP TABLE IF EXISTS `placed_furniture_migrated_v10`");
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `placed_furniture_migrated_v10` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`user_id` INTEGER NOT NULL, " +
                            "`furniture_id` INTEGER NOT NULL, " +
                            "`slot` TEXT NOT NULL, " +
                            "`placed_at` INTEGER NOT NULL, " +
                            "FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`furniture_id`) REFERENCES `furniture`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                            ")"
            );

            String placedAtExpr = placedFurnitureColumns.contains("placed_at") ? "COALESCE(`placed_at`, 0)" : "0";

            database.execSQL(
                    "INSERT OR REPLACE INTO `placed_furniture_migrated_v10` (`id`, `user_id`, `furniture_id`, `slot`, `placed_at`) " +
                            "SELECT `id`, `user_id`, `furniture_id`, `slot`, " + placedAtExpr + " " +
                            "FROM `placed_furniture` " +
                            "WHERE `user_id` IS NOT NULL AND `furniture_id` IS NOT NULL AND `slot` IS NOT NULL"
            );

            database.execSQL("DROP TABLE `placed_furniture`");
            database.execSQL("ALTER TABLE `placed_furniture_migrated_v10` RENAME TO `placed_furniture`");
        }

        database.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_placed_furniture_user_id` " +
                        "ON `placed_furniture` (`user_id`)"
        );
        database.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_placed_furniture_furniture_id` " +
                        "ON `placed_furniture` (`furniture_id`)"
        );
        database.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_placed_furniture_user_id_slot` " +
                        "ON `placed_furniture` (`user_id`, `slot`)"
        );
    }

    @NonNull
    private static Set<String> getTableColumns(@NonNull SupportSQLiteDatabase database, @NonNull String tableName) {
        Set<String> columns = new HashSet<>();
        Cursor cursor = database.query("PRAGMA table_info(`" + tableName + "`)");

        try {
            int nameColumn = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                columns.add(cursor.getString(nameColumn));
            }
        } finally {
            cursor.close();
        }

        return columns;
    }

    private static void seedFurnitureCatalog(SupportSQLiteDatabase db) {
        db.beginTransaction();
        try {
            Set<String> existingImageRefs = new HashSet<>();
            Cursor cursor = db.query("SELECT image_ref FROM furniture");

            try {
                while (cursor.moveToNext()) {
                    String imageRef = cursor.getString(0);
                    if (imageRef != null) {
                        existingImageRefs.add(imageRef);
                    }
                }
            } finally {
                cursor.close();
            }

            insertFurnitureIfMissing(db, existingImageRefs,
                    "Silla Madera", 50, "Basico", "furn_chair_wood", null, null);
            insertFurnitureIfMissing(db, existingImageRefs,
                    "Planta", 30, "Decoracion", "furn_plant_small", null, null);
            insertFurnitureIfMissing(db, existingImageRefs,
                    "PC Gamer", 500, "Tecnologia", "furn_pc_gamer", null, null);
            insertFurnitureIfMissing(db, existingImageRefs,
                    "Lampara", 80, "Iluminacion", "furn_lamp_desk", null, null);
            insertFurnitureIfMissing(db, existingImageRefs,
                    "Estanteria", 120, "Almacenaje", "furn_shelf", null, null);
            insertFurnitureIfMissing(db, existingImageRefs,
                    "Cama Comoda", 300, "Descanso", "furn_bed", null, null);
            insertFurnitureIfMissing(db, existingImageRefs,
                    "Alfombra", 40, "Decoracion", "furn_rug", null, null);
            insertFurnitureIfMissing(db, existingImageRefs,
                    "Ventilador Eco", 150, "Sostenibilidad", "furn_fan_eco", null, null);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static void insertFurnitureIfMissing(
            SupportSQLiteDatabase db,
            Set<String> existingImageRefs,
            String name,
            int price, String category,
            String imageRef,
            String description,
            String type
    ) {
        if (existingImageRefs.contains(imageRef)) {
            return;
        }

        db.execSQL(
                "INSERT INTO furniture (name, price, category, image_ref, description, type) VALUES (?, ?, ?, ?, ?, ?)",
                new Object[]{name, price, category, imageRef, description, type}
        );

        existingImageRefs.add(imageRef);
    }

    private static void ensureFurnitureCatalogSeeded(SupportSQLiteDatabase db) {
        seedFurnitureCatalog(db);
    }


    private static void repairLegacyTaskFrequencies(SupportSQLiteDatabase db) {
        db.execSQL(
                "UPDATE tasks SET frequency = ?, difficulty = ?, isCompleted = 0, last_completed_at = 0 " +
                        "WHERE title = ? AND description = ? AND reward_xp = ? AND reward_berries = ? " +
                        "AND (frequency IS NULL OR frequency = ? OR frequency = '' OR frequency = ?)",
                new Object[]{
                        Task.FREQUENCY_DAILY,
                        Task.DIFFICULTY_EASY,
                        "Beber agua",
                        "Empieza el dia cuidandote",
                        10,
                        5,
                        Task.FREQUENCY_ONCE,
                        "Normal"
                }
        );

        db.execSQL(
                "UPDATE tasks SET frequency = ?, difficulty = ?, isCompleted = 0, last_completed_at = 0 " +
                        "WHERE title = ? AND description = ? AND reward_xp = ? AND reward_berries = ? " +
                        "AND (frequency IS NULL OR frequency = ? OR frequency = '' OR frequency = ?)",
                new Object[]{
                        Task.FREQUENCY_DAILY,
                        Task.DIFFICULTY_EASY,
                        "Planificar el dia",
                        "Anota tus 3 prioridades",
                        15,
                        8,
                        Task.FREQUENCY_ONCE,
                        "Normal"
                }
        );

        db.execSQL(
                "UPDATE tasks SET frequency = ?, difficulty = ?, isCompleted = 0, last_completed_at = 0 " +
                        "WHERE title = ? AND description = ? AND reward_xp = ? AND reward_berries = ? " +
                        "AND (frequency IS NULL OR frequency = ? OR frequency = '' OR frequency = ?)",
                new Object[]{
                        Task.FREQUENCY_DAILY,
                        Task.DIFFICULTY_MEDIUM,
                        "Mover el cuerpo",
                        "Da un paseo corto o estira",
                        20,
                        10,
                        Task.FREQUENCY_ONCE,
                        "Normal"
                }
        );

        db.execSQL(
                "UPDATE tasks SET frequency = ? WHERE frequency = ?",
                new Object[]{Task.FREQUENCY_ONCE, "Normal"}
        );
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
                repairLegacyTaskFrequencies(db);
            });
        }
    };
}
