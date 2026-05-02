package com.irenaprokhyra.levelife.model;

final class DatabaseSql {
    static final String CREATE_TASK_USER_ID_INDEX_SQL =
            "CREATE INDEX IF NOT EXISTS `index_tasks_user_id` ON `tasks` (`user_id`)";

    private DatabaseSql() {
    }
}
