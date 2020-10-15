package com.deba1.res2rant.ui.customer;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.deba1.res2rant.AppHelper;
import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Food;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class FoodMenuListAdapter extends RecyclerView.Adapter<FoodMenuListAdapter.ViewHolder> implements Filterable {
    private List<Food> foods;
    private List<Food> foodsFiltered;
    private Fragment fragmentManager;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String keyword = charSequence.toString();
                if (keyword.isEmpty())
                    foodsFiltered = foods;
                else {
                    List<Food> filteredList = new ArrayList<>();
                    for (Food row : foods) {
                        if (row.name.toLowerCase().contains(keyword.toLowerCase()))
                            filteredList.add(row);
                    }
                    foodsFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = foodsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                foodsFiltered = (ArrayList<Food>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

    public void add(Food food) {
        foods.add(food);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        foods.remove(index);
        notifyItemRemoved(index);
    }

    public FoodMenuListAdapter(List<Food> foodList, Fragment manager) {
        this.foodsFiltered = foodList;
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
        final Food selectedFood = this.foodsFiltered.get(position);
        holder.foodItemName.setText(selectedFood.name);
        holder.foodItemDesc.setText(selectedFood.description);
        holder.foodItemPrice.setText(String.format("৳ %s", selectedFood.price));
        /*if (position%2==0)
            holder.layout.setBackgroundResource(R.drawable.food_item_background_even);
        else holder.layout.setBackgroundResource(R.drawable.bg_item_disabled);*/

        File cacheDir = new File(fragmentManager.getContext().getCacheDir(), "foodThumbs");
        if (!cacheDir.exists())
            cacheDir.mkdir();
        final File file = new File(cacheDir, selectedFood.id);
        if (!file.exists()) {
            storage.getReference(selectedFood.imagePath)
                .getBytes(2 * 1024 * 1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(bytes);
                            AppHelper.setDrawable(holder.foodItemImage, file.getPath());
                            if (selectedFood.available) {
                                FoodItemDialog foodItemDialog = new FoodItemDialog(fragmentManager.getContext(), selectedFood, holder.foodItemImage.getDrawable());
                                AppHelper.setDialogEvent(holder.layout, fragmentManager.getParentFragmentManager(), foodItemDialog);
                            }
                            else {
                                holder.layout.setBackgroundColor(Color.GRAY);
                                holder.layout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder alert = new AlertDialog.Builder(fragmentManager.getContext());
                                        alert.setMessage(R.string.food_unavailable);
                                        alert.setNegativeButton(R.string.ok, null);
                                        alert.show();
                                    }
                                });
                            }
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
            if (selectedFood.available) {
                FoodItemDialog foodItemDialog = new FoodItemDialog(fragmentManager.getContext(), selectedFood, holder.foodItemImage.getDrawable());
                AppHelper.setDialogEvent(holder.layout, fragmentManager.getParentFragmentManager(), foodItemDialog);
            }
            else {
                holder.layout.setBackgroundColor(Color.GRAY);
                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(fragmentManager.getContext());
                        alert.setMessage(R.string.food_unavailable);
                        alert.setNegativeButton(R.string.ok, null);
                        alert.show();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return foodsFiltered.size();
    }
}
