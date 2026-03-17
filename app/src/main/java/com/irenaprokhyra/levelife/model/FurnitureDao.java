package com.irenaprokhyra.levelife.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FurnitureDao {
    // Usamos REPLACE por si actualizamos precios en el futuro
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFurniture(Furniture furniture);

    @Query("SELECT * FROM furniture ORDER BY price ASC")
    List<Furniture> getAllFurniture();

    @Query("SELECT * FROM furniture WHERE category = :categoryName")
    List<Furniture> getFurnitureByCategory(String categoryName);

    // >_ METODO PARA COMPRAR _<
    // Necesitamos recuperar el objeto mueble cuando el usuario hace click en él
    @Query("SELECT * FROM furniture WHERE id = :id LIMIT 1")
    Furniture getFurnitureById(int id);

    // >_ Consulta para la Tienda (Solo IDs) _<
    // Devuelve una lista rápida de números (IDs) de los muebles que el usuario ya ha comprado
    @Query("SELECT furnitureId FROM user_furniture_cross_ref WHERE userId = :userId")
    List<Integer> getOwnedFurnitureIds(int userId);

    // >_ Consulta Relacional (JOIN) para el Inventario _<
    // Une la tabla de muebles con la tabla cruzada para sacar los objetos Furniture completos
    @Query("SELECT f.* FROM furniture f " +
            "INNER JOIN user_furniture_cross_ref crossRef ON f.id = crossRef.furnitureId " +
            "WHERE crossRef.userId = :userId")
    List<Furniture> getInventoryForUser(int userId);
}
