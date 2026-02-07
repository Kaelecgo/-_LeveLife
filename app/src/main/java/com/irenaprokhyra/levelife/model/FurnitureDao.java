package com.irenaprokhyra.levelife.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FurnitureDao {
    @Insert
    void insertFurniture(Furniture furniture);

    @Query("SELECT * FROM furniture")
    List<Furniture> getAllFurniture();

    @Query("SELECT * FROM furniture WHERE category = :categoryName")
    List<Furniture> getFurnitureByCategory(String categoryName);

}
