package com.deba1.res2rant.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Food {
    public String id;
    public String name;
    public String description;
    public float price;
    public String imagePath;
    public boolean available;
    public int totalPurchase;

    public Food() {}

    public Food(DocumentSnapshot snapshot) {
        Food f = snapshot.toObject(Food.class);
        assert f != null;

        this.id = snapshot.getId();
        this.name = f.name;
        this.imagePath = f.imagePath;
        this.description = f.description;
        this.price = f.price;
        this.available = f.available;
        this.totalPurchase = f.totalPurchase;
    }
}
