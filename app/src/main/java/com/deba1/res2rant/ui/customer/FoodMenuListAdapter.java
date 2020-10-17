package com.deba1.res2rant.ui.customer;

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

import com.deba1.res2rant.AppHelper;
import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Food;
import com.deba1.res2rant.ui.common.EditDialogBox;
import com.deba1.res2rant.ui.customer.FoodMenuFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class FoodMenuListAdapter extends RecyclerView.Adapter<FoodMenuListAdapter.ViewHolder> {
    private List<Food> foods;
    private FoodMenuFragment fragmentManager;
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

    public FoodMenuListAdapter(List<Food> foodList, FoodMenuFragment manager) {
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

        File cacheDir = new File(fragmentManager.getContext().getCacheDir(), "foodThumbs");
        if (!cacheDir.exists())
            cacheDir.mkdir();
        final File file = new File(cacheDir, foods.get(position).id);
        if (!file.exists()) {
            storage.getReference(foods.get(position).imagePath)
                .getBytes(2 * 1024 * 1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(bytes);
                            AppHelper.setDrawable(holder.foodItemImage, file.getPath());
                            FoodItemDialog foodItemDialog = new FoodItemDialog(foods.get(position), holder.foodItemImage.getDrawable());
                            AppHelper.setDialogEvent(holder.layout, fragmentManager.getParentFragmentManager(), foodItemDialog);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
        }
        else {
            AppHelper.setDrawable(holder.foodItemImage, file.getPath());
            FoodItemDialog foodItemDialog = new FoodItemDialog(foods.get(position), holder.foodItemImage.getDrawable());
            AppHelper.setDialogEvent(holder.layout, fragmentManager.getParentFragmentManager(), foodItemDialog);
        }
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
}
