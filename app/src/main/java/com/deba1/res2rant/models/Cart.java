package com.deba1.res2rant.models;

import java.util.List;

public class Cart {
    public List<CartItem> items;
    public static class CartItem {
        public String foodId;
        public int count;
        public String note;
        public String table;
        public String foodName;

        public CartItem() {}

        public CartItem(String foodId, String foodName, int count, String note, String table) {
            this.foodId = foodId;
            this.foodName = foodName;
            this.count = count;
            this.note = note;
            this.table = table;
        }

        public CartItem(Food food, int count, String note, String table) {
            this.foodId = food.id;
            this.foodName = food.name;
            this.count = count;
            this.note = note;
            this.table = table;
        }
    }
}
