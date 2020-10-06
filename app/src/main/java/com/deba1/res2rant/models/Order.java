package com.deba1.res2rant.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class Order {
    public String Id;
    public List<Cart.CartItem> cart;
    public String userId;
    public Timestamp orderedOn;
    public String status;
    public float price;
}
