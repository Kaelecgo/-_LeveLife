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

    @Query("SELECT * FROM tasks WHERE user_id = :userId " +
            "ORDER BY CASE frequency " +
            "WHEN 'Una vez' THEN 1 " +
            "WHEN 'Once' THEN 1 " +
            "WHEN 'Normal' THEN 1 " +
            "WHEN 'Diaria' THEN 2 " +
            "WHEN 'Daily' THEN 2 " +
            "WHEN 'Semanal' THEN 3 " +
            "WHEN 'Weekly' THEN 3 " +
            "WHEN 'Mensual' THEN 4 " +
            "WHEN 'Monthly' THEN 4 " +
            "ELSE 5 END, id ASC")
    LiveData<List<Task>> getTasksByUserIdLiveData(int userId);
}
