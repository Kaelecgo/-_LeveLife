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
    long insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    Task getTaskById(int id);

    @Query("SELECT * FROM tasks WHERE user_id = :userId ORDER BY id ASC")
    LiveData<List<Task>> getTasksByUserIdLiveData(int userId);
}
