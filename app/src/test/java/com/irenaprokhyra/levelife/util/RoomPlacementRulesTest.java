package com.irenaprokhyra.levelife.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.model.PlacedFurniture;

import org.junit.Test;

import java.util.List;

public class RoomPlacementRulesTest {

    @Test
    public void rug_onlyAllowsRoomCenter() {
        Furniture rug = new Furniture();
        rug.setType(Furniture.TYPE_RUG);
        rug.setImageRef("furn_rug");

        List<String> allowedSlots = RoomPlacementRules.getAllowedSlots(rug);

        assertEquals(1, allowedSlots.size());
        assertEquals(PlacedFurniture.SLOT_RUG_CENTER, allowedSlots.get(0));
    }

    @Test
    public void pc_allowsBothSurfaceAnchors() {
        Furniture pc = new Furniture();
        pc.setType(Furniture.TYPE_PC);
        pc.setImageRef("furn_pc_gamer");

        List<String> allowedSlots = RoomPlacementRules.getAllowedSlots(pc);

        assertEquals(2, allowedSlots.size());
        assertTrue(allowedSlots.contains(PlacedFurniture.SLOT_SURFACE_LEFT));
        assertTrue(allowedSlots.contains(PlacedFurniture.SLOT_SURFACE_RIGHT));
    }

    @Test
    public void legacyDeskSlot_forLamp_mapsToSurfaceRight() {
        String normalizedSlot = RoomPlacementRules.normalizeStoredSlot(
                PlacedFurniture.SLOT_DESK,
                Furniture.TYPE_LAMP,
                "furn_lamp_desk"
        );

        assertEquals(PlacedFurniture.SLOT_SURFACE_RIGHT, normalizedSlot);
    }

    @Test
    public void legacyFloorSlot_forBed_mapsToBedNook() {
        String normalizedSlot = RoomPlacementRules.normalizeStoredSlot(
                PlacedFurniture.SLOT_FLOOR,
                Furniture.TYPE_BED,
                "furn_bed"
        );

        assertEquals(PlacedFurniture.SLOT_BED_NOOK, normalizedSlot);
    }

    @Test
    public void legacyDeskSlot_forShelf_mapsToWallShowcase() {
        String normalizedSlot = RoomPlacementRules.normalizeStoredSlot(
                PlacedFurniture.SLOT_DESK,
                Furniture.TYPE_SHELF,
                "furn_shelf"
        );

        assertEquals(PlacedFurniture.SLOT_WALL_SHOWCASE, normalizedSlot);
    }

    @Test
    public void legacyWallSlot_forPlant_mapsToFloorLeft() {
        String normalizedSlot = RoomPlacementRules.normalizeStoredSlot(
                PlacedFurniture.SLOT_WALL,
                Furniture.TYPE_PLANT,
                "furn_plant_small"
        );

        assertEquals(PlacedFurniture.SLOT_FLOOR_LEFT, normalizedSlot);
    }
}
