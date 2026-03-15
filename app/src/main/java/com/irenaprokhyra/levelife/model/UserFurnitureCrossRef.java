package com.irenaprokhyra.levelife.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

// Definimos las dos columnas como clave primaria conjunta para que no compre dos veces lo mismo
@Entity(tableName = "user_furniture_cross_ref",
        primaryKeys = {"userId", "furnitureId"},
        indices = {@Index(value = "furnitureId")},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Furniture.class,
                        parentColumns = "id",
                        childColumns = "furnitureId",
                        onDelete = ForeignKey.CASCADE)
        })
public class UserFurnitureCrossRef {

    public int userId;
    public int furnitureId;

    public UserFurnitureCrossRef() {}

    @Ignore
    public UserFurnitureCrossRef(int userId, int furnitureId) {
        this.userId = userId;
        this.furnitureId = furnitureId;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFurnitureId() { return furnitureId; }
    public void setFurnitureId(int furnitureId) { this.furnitureId = furnitureId; }
}
