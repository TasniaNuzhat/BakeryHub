package com.example.bakeryhub;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Product {
    private int id;
    private String name;
    private double price;
    private byte[] image;


    public Product(int id, String name, double price, byte[] image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public byte[] getImage() {
        return image;
    }


    public Bitmap getImageAsBitmap() {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
