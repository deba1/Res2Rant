package com.deba1.res2rant.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class Food {
    public String id;
    public String name;
    public String description;
    public float price;
    public String imagePath;

    public Food() {}

    public Food(DocumentSnapshot snapshot) {
        id = snapshot.getId();
        name = snapshot.getString("name");
        description = snapshot.getString("description");
        imagePath = snapshot.getString("imagePath");
        price = snapshot.getLong("price").floatValue();
    }
}
