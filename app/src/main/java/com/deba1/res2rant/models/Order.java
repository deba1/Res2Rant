package com.deba1.res2rant.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class Order {
    public String Id;
    public List<Cart.CartItem> cart;
    public String userId;
    public Timestamp orderedOn;
    public String status;
    public float price;

    public Order() {}

    public Order(QueryDocumentSnapshot snapshot) {
        Order o = snapshot.toObject(Order.class);
        this.Id = snapshot.getId();
        this.status = o.status;
        this.price = o.price;
        this.userId = o.userId;
        this.cart = o.cart;
    }
}
