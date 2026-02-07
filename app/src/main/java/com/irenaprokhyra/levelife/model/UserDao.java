package com.irenaprokhyra.levelife.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users LIMIT 1")
    User getUser();

    @Query("UPDATE users SET berries = berries + :amount WHERE id = :userId")
    void updateBerries(int userId, int amount);
}
