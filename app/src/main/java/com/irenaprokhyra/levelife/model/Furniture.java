package com.irenaprokhyra.levelife.model;

public class Furniture {
    private int id;
    private String name;
    private int price;
    private String imageRef;
    private String category;

    public Furniture() {}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price;}
    public void setPrice(int price) { this.price = price; }
    public String getImageRef() {return imageRef; }
    public void setImageRef(String imageRef) { this.imageRef = imageRef; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}