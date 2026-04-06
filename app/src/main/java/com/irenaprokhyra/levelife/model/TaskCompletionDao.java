package com.irenaprokhyra.levelife.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface TaskCompletionDao {

    @Insert
    long insertCompletion(TaskCompletion completion);

    @Query("SELECT EXISTS(SELECT 1 FROM task_completions WHERE task_id = :taskId AND completed_at >= :periodStart)")
    boolean hasCompletionSince(int taskId, long periodStart);

    @Query("SELECT COUNT(*) FROM task_completions WHERE task_id = :taskId")
    int countCompletionsForTask(int taskId);
}
