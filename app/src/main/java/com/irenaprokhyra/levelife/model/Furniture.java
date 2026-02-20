package com.irenaprokhyra.levelife.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;


@Entity(tableName = "furniture")
public class Furniture {
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Guardaremos el NOMBRE del recurso (ej: "ic_chair_wood"), no el ID numérico
    @ColumnInfo(name = "image_ref")
    private String imageRef;

    private String name;
    private int price;
    private String category;

    // Constructor vacio obligatorio para Room
    public Furniture() {}

    // >_ CONSTRUCTOR DE CONVENIENCIA _<
    // Nos permitirá llenar la tienda en una sola línea de código
    @Ignore
    public Furniture(String name, int price, String category, String imageRef) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageRef = imageRef;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price;}
    // >_ METODO DE VALIDACION _<
    public void setPrice(int price) {
        if (price < 0) {
            this.price = 0; // Evitar precios negativos
        } else {
            this.price = price; }
        }

    public String getImageRef() {return imageRef; }
    public void setImageRef(String imageRef) { this.imageRef = imageRef; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}