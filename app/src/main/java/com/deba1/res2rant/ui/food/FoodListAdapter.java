package com.deba1.res2rant.ui.food;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Food;
import com.deba1.res2rant.ui.common.EditDialogBox;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayInputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {
    private List<Food> foods;
    private FoodFragment fragmentManager;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodItemName;
        public TextView foodItemDesc;
        public ImageView foodItemImage;
        public TextView foodItemPrice;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            foodItemName = v.findViewById(R.id.foodItemName);
            foodItemDesc = v.findViewById(R.id.foodItemDesc);
            foodItemImage = v.findViewById(R.id.foodItemImage);
            foodItemPrice = v.findViewById(R.id.foodItemPrice);
        }
    }

    public void add(int index, Food food) {
        foods.add(index, food);
        notifyItemInserted(index);
    }

    public void remove(int index) {
        foods.remove(index);
        notifyItemRemoved(index);
    }

    public FoodListAdapter(List<Food> foodList, FoodFragment manager) {
        foods = foodList;
        fragmentManager = manager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.component_food_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.foodItemName.setText(foods.get(position).name);
        holder.foodItemDesc.setText(foods.get(position).description);
        holder.foodItemPrice.setText(String.format("৳ %s", foods.get(position).price));
        if (position%2==0)
            holder.layout.setBackgroundResource(R.drawable.food_item_background_even);
        else holder.layout.setBackgroundResource(R.drawable.food_item_background_odd);
        storage.getReference(foods.get(position).imagePath)
                .getBytes(1048576)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                        Drawable image = Drawable.createFromStream(stream, "foodImage");
                        holder.foodItemImage.setImageDrawable(image);

                        final FoodDetailsDialog dialogFragment = new FoodDetailsDialog(foods.get(position).id, foods.get(position).name, foods.get(position).description, holder.foodItemImage.getDrawable(), foods.get(position).price);

                        holder.layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogFragment.show(fragmentManager.getParentFragmentManager(), "Food");
                            }
                        });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
}
