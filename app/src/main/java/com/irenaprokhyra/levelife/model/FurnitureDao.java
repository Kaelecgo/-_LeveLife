package com.irenaprokhyra.levelife.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FurnitureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFurniture(Furniture furniture);

    @Query("SELECT * FROM furniture ORDER BY CASE WHEN currency = 'eco' THEN 1 ELSE 0 END, price ASC, name ASC")
    LiveData<List<Furniture>> getAllFurnitureLiveData();

    @Query("SELECT * FROM furniture WHERE category = :categoryName")
    List<Furniture> getFurnitureByCategory(String categoryName);

    @Query("SELECT * FROM furniture WHERE id = :id LIMIT 1")
    Furniture getFurnitureById(int id);

    @Query("SELECT furnitureId FROM user_furniture_cross_ref WHERE userId = :userId")
    List<Integer> getOwnedFurnitureIds(int userId);

    @Query("SELECT COUNT(*) FROM user_furniture_cross_ref WHERE userId = :userId AND furnitureId = :furnitureId")
    int countUserFurniture(int userId, int furnitureId);

    @Query("SELECT f.* FROM furniture f " +
            "INNER JOIN user_furniture_cross_ref crossRef ON f.id = crossRef.furnitureId " +
            "WHERE crossRef.userId = :userId")
    LiveData<List<Furniture>> getInventoryForUserLiveData(int userId);

    @Query("SELECT * FROM furniture ORDER BY CASE WHEN currency = 'eco' THEN 1 ELSE 0 END, price ASC, name ASC")
    List<Furniture> getAllFurniture();

    @Query("SELECT f.* FROM furniture f " +
            "INNER JOIN user_furniture_cross_ref crossRef ON f.id = crossRef.furnitureId " +
            "WHERE crossRef.userId = :userId")
    List<Furniture> getInventoryForUser(int userId);
}
