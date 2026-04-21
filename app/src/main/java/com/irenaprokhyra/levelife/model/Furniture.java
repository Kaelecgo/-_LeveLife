package com.irenaprokhyra.levelife.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;


@Entity(tableName = "furniture")
public class Furniture {
    public static final String CURRENCY_BERRIES = "berries";
    public static final String CURRENCY_ECO = "eco";
    public static final String TYPE_CHAIR = "chair";
    public static final String TYPE_PLANT = "plant";
    public static final String TYPE_PC = "pc";
    public static final String TYPE_LAMP = "lamp";
    public static final String TYPE_SHELF = "shelf";
    public static final String TYPE_BED = "bed";
    public static final String TYPE_RUG = "rug";
    public static final String TYPE_FAN = "fan";

    @PrimaryKey(autoGenerate = true)
    private int id;

    // Guardaremos el NOMBRE del recurso (ej: "ic_chair_wood"), no el ID numérico
    @ColumnInfo(name = "image_ref")
    private String imageRef;

    private String name;
    private int price;
    private String category;
    private String description;
    private String type;

    @NonNull
    @ColumnInfo(name = "currency", defaultValue = "'berries'")
    private String currency = CURRENCY_BERRIES;

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
        this.currency = CURRENCY_BERRIES;
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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return normalizeType(type); }
    public void setType(String type) { this.type = normalizeType(type); }

    @NonNull
    public String getCurrency() { return normalizeCurrency(currency); }
    public void setCurrency(String currency) { this.currency = normalizeCurrency(currency); }

    public boolean isEcoCurrency() {
        return CURRENCY_ECO.equals(getCurrency());
    }

    private static String normalizeCurrency(String currency) {
        if (CURRENCY_ECO.equalsIgnoreCase(currency)) {
            return CURRENCY_ECO;
        }
        return CURRENCY_BERRIES;
    }

    private static String normalizeType(String type) {
        if (type == null) {
            return "";
        }

        String normalized = type.trim().toLowerCase();
        switch (normalized) {
            case TYPE_CHAIR:
            case TYPE_PLANT:
            case TYPE_PC:
            case TYPE_LAMP:
            case TYPE_SHELF:
            case TYPE_BED:
            case TYPE_RUG:
            case TYPE_FAN:
                return normalized;
            default:
                return normalized;
        }
    }
}
