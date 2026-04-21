package com.irenaprokhyra.levelife.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.model.PlacedFurniture;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class RoomPlacementRules {

    private static final List<String> BED_SLOTS =
            Collections.singletonList(PlacedFurniture.SLOT_BED_NOOK);
    private static final List<String> RUG_SLOTS =
            Collections.singletonList(PlacedFurniture.SLOT_RUG_CENTER);
    private static final List<String> SHELF_SLOTS =
            Collections.singletonList(PlacedFurniture.SLOT_WALL_SHOWCASE);
    private static final List<String> FLOOR_SLOTS =
            Arrays.asList(PlacedFurniture.SLOT_FLOOR_LEFT, PlacedFurniture.SLOT_FLOOR_RIGHT);
    private static final List<String> SURFACE_SLOTS =
            Arrays.asList(PlacedFurniture.SLOT_SURFACE_LEFT, PlacedFurniture.SLOT_SURFACE_RIGHT);
    private static final List<String> DEFAULT_SLOTS =
            Collections.singletonList(PlacedFurniture.SLOT_FLOOR_LEFT);

    private RoomPlacementRules() {
    }

    @NonNull
    public static List<String> getAllowedSlots(@Nullable Furniture furniture) {
        if (furniture == null) {
            return DEFAULT_SLOTS;
        }
        return getAllowedSlots(furniture.getType(), furniture.getImageRef());
    }

    @NonNull
    public static String normalizeStoredSlot(
            @Nullable String rawSlot,
            @Nullable Furniture furniture
    ) {
        if (furniture == null) {
            return normalizeStoredSlot(rawSlot, null, null);
        }
        return normalizeStoredSlot(rawSlot, furniture.getType(), furniture.getImageRef());
    }

    @NonNull
    public static String normalizeStoredSlot(
            @Nullable String rawSlot,
            @Nullable String type,
            @Nullable String imageRef
    ) {
        if (isCanonicalSlot(rawSlot)) {
            return rawSlot;
        }

        List<String> allowedSlots = getAllowedSlots(type, imageRef);
        if (allowedSlots.size() == 1) {
            return allowedSlots.get(0);
        }

        if (PlacedFurniture.SLOT_WALL.equals(rawSlot)) {
            if (allowedSlots.contains(PlacedFurniture.SLOT_WALL_SHOWCASE)) {
                return PlacedFurniture.SLOT_WALL_SHOWCASE;
            }
            if (allowedSlots.contains(PlacedFurniture.SLOT_SURFACE_LEFT)) {
                return PlacedFurniture.SLOT_SURFACE_LEFT;
            }
        }

        if (PlacedFurniture.SLOT_DESK.equals(rawSlot)) {
            if (allowedSlots.contains(PlacedFurniture.SLOT_SURFACE_RIGHT)) {
                return PlacedFurniture.SLOT_SURFACE_RIGHT;
            }
            if (allowedSlots.contains(PlacedFurniture.SLOT_SURFACE_LEFT)) {
                return PlacedFurniture.SLOT_SURFACE_LEFT;
            }
        }

        if (PlacedFurniture.SLOT_DECOR.equals(rawSlot)) {
            if (allowedSlots.contains(PlacedFurniture.SLOT_SURFACE_RIGHT)) {
                return PlacedFurniture.SLOT_SURFACE_RIGHT;
            }
            if (allowedSlots.contains(PlacedFurniture.SLOT_FLOOR_RIGHT)) {
                return PlacedFurniture.SLOT_FLOOR_RIGHT;
            }
        }

        if (PlacedFurniture.SLOT_FLOOR.equals(rawSlot)) {
            if (allowedSlots.contains(PlacedFurniture.SLOT_FLOOR_LEFT)) {
                return PlacedFurniture.SLOT_FLOOR_LEFT;
            }
        }

        return allowedSlots.get(0);
    }

    public static int getSlotLabelRes(@Nullable String slot) {
        if (slot == null) {
            return 0;
        }

        switch (slot) {
            case PlacedFurniture.SLOT_WALL_SHOWCASE:
                return R.string.inventory_slot_wall_showcase;
            case PlacedFurniture.SLOT_BED_NOOK:
                return R.string.inventory_slot_bed_nook;
            case PlacedFurniture.SLOT_RUG_CENTER:
                return R.string.inventory_slot_rug_center;
            case PlacedFurniture.SLOT_FLOOR_LEFT:
                return R.string.inventory_slot_floor_left;
            case PlacedFurniture.SLOT_FLOOR_RIGHT:
                return R.string.inventory_slot_floor_right;
            case PlacedFurniture.SLOT_SURFACE_LEFT:
                return R.string.inventory_slot_surface_left;
            case PlacedFurniture.SLOT_SURFACE_RIGHT:
                return R.string.inventory_slot_surface_right;
            default:
                return 0;
        }
    }

    private static boolean isCanonicalSlot(@Nullable String slot) {
        return PlacedFurniture.SLOT_WALL_SHOWCASE.equals(slot)
                || PlacedFurniture.SLOT_BED_NOOK.equals(slot)
                || PlacedFurniture.SLOT_RUG_CENTER.equals(slot)
                || PlacedFurniture.SLOT_FLOOR_LEFT.equals(slot)
                || PlacedFurniture.SLOT_FLOOR_RIGHT.equals(slot)
                || PlacedFurniture.SLOT_SURFACE_LEFT.equals(slot)
                || PlacedFurniture.SLOT_SURFACE_RIGHT.equals(slot);
    }

    @NonNull
    private static List<String> getAllowedSlots(@Nullable String type, @Nullable String imageRef) {
        String placementKey = resolvePlacementKey(type, imageRef);

        switch (placementKey) {
            case Furniture.TYPE_BED:
                return BED_SLOTS;
            case Furniture.TYPE_RUG:
                return RUG_SLOTS;
            case Furniture.TYPE_SHELF:
                return SHELF_SLOTS;
            case Furniture.TYPE_PC:
            case Furniture.TYPE_LAMP:
                return SURFACE_SLOTS;
            case Furniture.TYPE_CHAIR:
            case Furniture.TYPE_PLANT:
            case Furniture.TYPE_FAN:
                return FLOOR_SLOTS;
            default:
                return DEFAULT_SLOTS;
        }
    }

    @NonNull
    private static String resolvePlacementKey(@Nullable String type, @Nullable String imageRef) {
        if (type != null && !type.trim().isEmpty()) {
            return type.trim().toLowerCase(Locale.ROOT);
        }

        String normalizedImageRef = imageRef == null ? "" : imageRef.trim().toLowerCase(Locale.ROOT);
        if (normalizedImageRef.contains("bed")) {
            return Furniture.TYPE_BED;
        }
        if (normalizedImageRef.contains("rug")) {
            return Furniture.TYPE_RUG;
        }
        if (normalizedImageRef.contains("shelf")) {
            return Furniture.TYPE_SHELF;
        }
        if (normalizedImageRef.contains("pc")) {
            return Furniture.TYPE_PC;
        }
        if (normalizedImageRef.contains("lamp")) {
            return Furniture.TYPE_LAMP;
        }
        if (normalizedImageRef.contains("chair")) {
            return Furniture.TYPE_CHAIR;
        }
        if (normalizedImageRef.contains("plant")) {
            return Furniture.TYPE_PLANT;
        }
        if (normalizedImageRef.contains("fan")) {
            return Furniture.TYPE_FAN;
        }
        return "";
    }
}
