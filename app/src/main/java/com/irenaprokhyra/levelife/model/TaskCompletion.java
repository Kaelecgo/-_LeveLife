package com.irenaprokhyra.levelife.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "task_completions",
        foreignKeys = {
                @ForeignKey(
                        entity = Task.class,
                        parentColumns = "id",
                        childColumns = "task_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"user_id"}),
                @Index(value = {"task_id", "completed_at"})
        }
)

public class TaskCompletion {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "task_id")
    private int taskId;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "completed_at")
    private long completedAt;

    public TaskCompletion() {
    }

    @Ignore
    public TaskCompletion(int taskId, int userId, long completedAt) {
        this.taskId = taskId;
        this.userId = userId;
        this.completedAt = completedAt;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }


}
