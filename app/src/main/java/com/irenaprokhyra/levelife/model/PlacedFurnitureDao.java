package com.irenaprokhyra.levelife.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlacedFurnitureDao {

    @Insert
    long insertPlacedFurniture(PlacedFurniture placedFurniture);

    @Update
    void updatePlacedFurniture(PlacedFurniture placedFurniture);

    @Query("SELECT * FROM placed_furniture WHERE user_id = :userId AND slot = :slot LIMIT 1")
    PlacedFurniture getPlacedFurnitureForSlot(int userId, String slot);

    @Query("SELECT * FROM placed_furniture WHERE user_id = :userId AND furniture_id = :furnitureId LIMIT 1")
    PlacedFurniture getPlacedFurnitureByFurnitureId(int userId, int furnitureId);

    @Query("SELECT * FROM placed_furniture WHERE user_id = :userId")
    List<PlacedFurniture> getPlacedFurnitureForUser(int userId);

    @Query("DELETE FROM placed_furniture WHERE user_id = :userId AND slot = :slot")
    void removePlacedFurnitureForSlot(int userId, String slot);

    @Query("DELETE FROM placed_furniture WHERE id = :placementId")
    void removePlacedFurnitureById(int placementId);

    @Query(
            "SELECT " +
                    "pf.slot AS slot, " +
                    "pf.placed_at AS placedAt, " +
                    "f.id AS furnitureId, " +
                    "f.name AS name, " +
                    "f.image_ref AS imageRef, " +
                    "f.type AS type, " +
                    "f.category AS category " +
                    "FROM placed_furniture pf " +
                    "INNER JOIN furniture f ON f.id = pf.furniture_id " +
                    "WHERE pf.user_id = :userId"
    )
    LiveData<List<PlacedFurnitureItem>> getPlacedFurnitureItemsForUserLiveData(int userId);
}
