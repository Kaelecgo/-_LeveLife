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

    // >_ METODO DE ORDENACIÓN INTELIGENTE _<
    // Muestra primero las pendientes (0) y luego las completadas (1)
    // Dentro de eso, las más nuevas primero
    @Query("SELECT * FROM tasks WHERE user_id = :userId ORDER BY isCompleted ASC, id DESC")
    List<Task> getTasksByUserId(int userId);

    @Query("SELECT * FROM tasks WHERE category = :categoryName")
    List<Task> getTasksByCategory(String categoryName);

    // >_ METODO DE ESTADÍSTICAS PARA DASHBOARD _<
    @Query("SELECT COUNT(*) FROM tasks WHERE user_id = :userId AND isCompleted = 1")
    int countCompletedTasks(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE user_id = :userId AND isCompleted = 0")
    int countPendingTasks(int userId);
}
