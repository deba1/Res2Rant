package com.deba1.res2rant.models;

import java.util.List;

public class Cart {
    public List<CartItem> items;
    public static class CartItem {
        public String foodId;
        public int count;
        public String note;
        public String table;
        public CartItem() {}
        public CartItem(String foodId, int count, String note, String table) {
            this.foodId = foodId;
            this.count = count;
            this.note = note;
            this.table = table;
        }
    }
    public static class FoodItem {
        public String foodName;
        public String note;
        public int count;
        public String table;
        public float price;
        public FoodItem(Food food, int count, String note, String table) {
            this.foodName = food.name;
            this.count = count;
            this.price = food.price * count;
            this.note = note;
            this.table = table;
        }
    }
}
