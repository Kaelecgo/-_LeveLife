package com.irenaprokhyra.levelife.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insertTask(Task task);
    @Update
    void updateTask(Task task);
    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM tasks WHERE user_id = :userId ORDER BY id DESC")
    List<Task> getTasksByUserId(int userId);

    @Query("SELECT * FROM tasks WHERE category = :categoryName")
    List<Task> getTasksByCategory(String categoryName);
}
