package com.deba1.res2rant.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Cart;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private List<Cart.FoodItem> items;
    private MyCartFragment fragment;
    //private FirebaseStorage storage = FirebaseStorage.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodNameView;
        public TextView noteView;
        public ImageView foodItemImage;
        public TextView countView;
        public TextView tableNoView;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            foodNameView = v.findViewById(R.id.cart_item_title);
            noteView = v.findViewById(R.id.cart_item_note);
            foodItemImage = v.findViewById(R.id.cart_item_image);
            countView = v.findViewById(R.id.cart_item_count);
            tableNoView = v.findViewById(R.id.cart_item_table);
        }
    }

    public void add(Cart.FoodItem food) {
        items.add(getItemCount(), food);
        notifyItemChanged(getItemCount());
    }

    public void add(int index, Cart.FoodItem food) {
        items.add(index, food);
        notifyItemInserted(index);
    }

    public void remove(int index) {
        items.remove(index);
        notifyItemRemoved(index);
    }

    public CartItemAdapter(List<Cart.FoodItem> foodList, MyCartFragment manager) {
        items = foodList;
        fragment = manager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.component_cart_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.foodNameView.setText(items.get(position).foodName);
        holder.noteView.setText(items.get(position).note);
        holder.countView.setText(String.format("Count: %s", items.get(position).count));
        holder.tableNoView.setText(items.get(position).table);
        if (position%2==0)
            holder.layout.setBackgroundResource(R.drawable.food_item_background_even);
        else holder.layout.setBackgroundResource(R.drawable.food_item_background_odd);
        //File cacheDir = new File(fragment.getContext().getCacheDir(), "foodThumbs");
        //if (!cacheDir.exists())
            //cacheDir.mkdir();
        //final File file = new File(cacheDir, items.get(position).id);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
