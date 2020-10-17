package com.deba1.res2rant.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderRaw implements Serializable {
    public String Id;
    public String orderedOn;
    public String status;
    public List<Cart.CartItem> cart;
    public float price;

    public OrderRaw() {}

    public OrderRaw(Order order) {
        this.Id = order.Id;
        this.status = order.status;
        this.price = order.price;
        this.cart = order.cart;
        this.orderedOn = new SimpleDateFormat("dd/MM/yy - hh:mm aa", Locale.ENGLISH).format(order.orderedOn.toDate());
    }
}
