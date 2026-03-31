package com.irenaprokhyra.levelife.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskCompletionDao {

    @Insert
    long insertCompletion(TaskCompletion completion);

    @Query("SELECT * FROM task_completions WHERE task_id = :taskId ORDER BY completed_at DESC LIMIT 1")
    TaskCompletion getLatestCompletionForTask(int taskId);

    @Query("SELECT COUNT(*) FROM task_completions WHERE task_id = :taskId")
    int countCompletionsForTask(int taskId);

    @Query("SELECT * FROM task_completions WHERE task_id = :taskId ORDER BY completed_at DESC")
    List<TaskCompletion> getCompletionsForTask(int taskId);

    @Query("SELECT EXISTS(SELECT 1 FROM task_completions WHERE task_id = :taskId AND completed_at >= :periodStart)")
    boolean hasCompletionSince(int taskId, long periodStart);

}
