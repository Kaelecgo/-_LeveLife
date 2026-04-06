package com.irenaprokhyra.levelife.model;

public class PlacedFurnitureItem {

    private String slot;
    private long placedAt;
    private int furnitureId;
    private String name, imageRef, type, category;

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getImageRef() { return imageRef; }

    public void setImageRef(String imageRef) { this.imageRef = imageRef; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getFurnitureId() { return furnitureId; }

    public void setFurnitureId(int furnitureId) { this.furnitureId = furnitureId; }

    public long getPlacedAt() { return placedAt; }

    public void setPlacedAt(long placedAt) { this.placedAt = placedAt; }

    public String getSlot() { return slot; }

    public void setSlot(String slot) { this.slot = slot; }
}
