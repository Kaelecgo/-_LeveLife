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

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getUserById(int id);

    @Query("SELECT * FROM users WHERE user_name = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("UPDATE users SET berries = berries + :amount WHERE id = :userId")
    void updateBerries(int userId, int amount);


}
