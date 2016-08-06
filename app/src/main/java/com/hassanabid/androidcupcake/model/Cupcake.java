package com.hassanabid.androidcupcake.model;

import io.realm.RealmObject;

/**
 * Created by hassanabid on 8/6/16.
 */
public class Cupcake extends RealmObject {

    public String name;
    public String price;
    public float rating;
    public String image;
    public String writer;
    public String createdAt;


    // Let your IDE generate getters and setters for you!
    // Or if you like you can even have public fields and no accessors!
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWriter() {
        return writer;
    }

    public void setWritere(String writer) {
        this.writer = writer;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


}
