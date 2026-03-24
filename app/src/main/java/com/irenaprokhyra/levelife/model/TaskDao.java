package com.irenaprokhyra.levelife.model;

import androidx.lifecycle.LiveData;
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

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY isCompleted ASC")
    LiveData<List<Task>> getTasksByUserIdLiveData(int userId);

    @Query("SELECT * FROM tasks WHERE category = :categoryName")
    List<Task> getTasksByCategory(String categoryName);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 1")
    int countCompletedTasks(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 0")
    int countPendingTasks(int userId);
}