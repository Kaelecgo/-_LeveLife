package com.irenaprokhyra.levelife.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "placed_furniture",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Furniture.class,
                        parentColumns = "id",
                        childColumns = "furniture_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"user_id"}),
                @Index(value = {"furniture_id"}),
                @Index(value = {"user_id", "slot"}, unique = true)
        }
)
public class PlacedFurniture {
    // Legacy generic slots kept for compatibility with already placed furniture.
    public static final String SLOT_FLOOR = "floor";
    public static final String SLOT_WALL = "wall";
    public static final String SLOT_DESK = "desk";
    public static final String SLOT_DECOR = "decor";

    // Canonical room anchors for the MVP room scene.
    public static final String SLOT_WALL_SHOWCASE = "wall_showcase";
    public static final String SLOT_BED_NOOK = "bed_nook";
    public static final String SLOT_RUG_CENTER = "rug_center";
    public static final String SLOT_FLOOR_LEFT = "floor_left";
    public static final String SLOT_FLOOR_RIGHT = "floor_right";
    public static final String SLOT_SURFACE_LEFT = "surface_left";
    public static final String SLOT_SURFACE_RIGHT = "surface_right";

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "furniture_id")
    private int furnitureId;

    @NonNull
    @ColumnInfo(name = "slot")
    private String slot;

    @ColumnInfo(name = "placed_at")
    private long placedAt;

    public PlacedFurniture() {}

    @Ignore
    public PlacedFurniture(int userId, int furnitureId, @NonNull String slot, long placedAt) {
        this.userId = userId;
        this.furnitureId = furnitureId;
        this.slot = slot;
        this.placedAt = placedAt;
    }

    public static boolean isValidSlot(String slot) {
        return SLOT_FLOOR.equals(slot)
                || SLOT_WALL.equals(slot)
                || SLOT_DESK.equals(slot)
                || SLOT_DECOR.equals(slot)
                || SLOT_WALL_SHOWCASE.equals(slot)
                || SLOT_BED_NOOK.equals(slot)
                || SLOT_RUG_CENTER.equals(slot)
                || SLOT_FLOOR_LEFT.equals(slot)
                || SLOT_FLOOR_RIGHT.equals(slot)
                || SLOT_SURFACE_LEFT.equals(slot)
                || SLOT_SURFACE_RIGHT.equals(slot);
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public int getUserId() {return userId;}

    public void setUserId(int userId) {this.userId = userId;}

    public int getFurnitureId() {return furnitureId;}

    public void setFurnitureId(int furnitureId) {this.furnitureId = furnitureId;}

    @NonNull
    public String getSlot() {return slot;}

    public void setSlot(@NonNull String slot) {this.slot = slot;}

    public long getPlacedAt() {return placedAt;}

    public void setPlacedAt(long placedAt) {this.placedAt = placedAt;}
}

