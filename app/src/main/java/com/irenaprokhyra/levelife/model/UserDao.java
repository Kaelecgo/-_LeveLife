package com.irenaprokhyra.levelife.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    // Si se intenta meter un usuario que ya existe, lo ignora o devuelve -1
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getUserById(int id);

    @Query("SELECT * FROM users WHERE user_name = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("UPDATE users SET berries = berries + :amount WHERE id = :userId")
    void updateBerries(int userId, int amount);

    // >_ METODO DE VALIDACIÓN DE REGISTRO _<
    // Devuelve 1 si existe, 0 si no. Útil para mostrar error "El usuario ya existe"
    @Query("SELECT COUNT(*) FROM users WHERE user_name = :username")
    int checkUserExists(String username);
}
