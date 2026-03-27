package com.irenaprokhyra.levelife.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);
    @Update
    void updateUser(User user);
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);
    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<User> getUserByIdLiveData(int id);
    @Query("SELECT * FROM users WHERE user_name = :username AND password = :password LIMIT 1")
    User login(String username, String password);
    @Query("UPDATE users SET berries = berries + :amount WHERE id = :userId")
    void updateBerries(int userId, int amount);
    @Query("SELECT COUNT(*) FROM users WHERE user_name = :username")
    int checkUserExists(String username);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUserFurnitureCrossRef(UserFurnitureCrossRef crossRef);
}