package com.irenaprokhyra.levelife.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseSqlTest {

    @Test
    public void createTaskUserIdIndexSqlExecutesInSqlite() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE tasks (id INTEGER PRIMARY KEY, user_id INTEGER NOT NULL)");
            statement.execute(DatabaseSql.CREATE_TASK_USER_ID_INDEX_SQL);

            try (ResultSet resultSet = statement.executeQuery("PRAGMA index_list(`tasks`)")) {
                boolean foundIndex = false;
                while (resultSet.next()) {
                    if ("index_tasks_user_id".equals(resultSet.getString("name"))) {
                        foundIndex = true;
                        break;
                    }
                }
                assertTrue("Expected the task user index to be created", foundIndex);
            }
        }
    }
}
