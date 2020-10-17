package com.deba1.res2rant.ui.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Cart;
import com.deba1.res2rant.models.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static android.app.Activity.RESULT_OK;

public class FoodItemDialog extends DialogFragment {
    private String foodId;
    private String foodName;
    private String foodDesc;
    private float foodPrice;
    private Drawable foodImage;
    private Spinner tableNo;
    private EditText orderNote;
    private TextView tableLabel;
    private int foodCount = 1;
    private int table = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public FoodItemDialog(Food food, Drawable foodImage) {
        foodId = food.id;
        this.foodName = food.name;
        this.foodDesc = food.description;
        this.foodPrice = food.price;
        this.foodImage = foodImage;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(foodName);
        final View view = getLayoutInflater().inflate(R.layout.food_menu_item_dialog, null);
        TextView fieldDesc = view.findViewById(R.id.food_menu_item_dialog_description);
        fieldDesc.setText(foodDesc);
        TextView fieldPrice = view.findViewById(R.id.food_menu_item_dialog_price);
        fieldPrice.setText(String.valueOf(foodPrice));
        ImageView foodImageView = view.findViewById(R.id.food_menu_item_dialog_image);
        foodImageView.setImageDrawable(foodImage);
        tableLabel = view.findViewById(R.id.food_menu_table_label);
        tableNo = view.findViewById(R.id.food_menu_item_dialog_table);
        tableNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0)
                    tableLabel.setTextColor(getResources().getColor(R.color.errorTextColor));
                else
                    tableLabel.setTextColor(getResources().getColor(R.color.textGray));
                table = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        orderNote = view.findViewById(R.id.food_menu_item_dialog_note);
        SeekBar itemSelectorView = view.findViewById(R.id.food_menu_item_dialog_count);
        final TextView itemCounterView = view.findViewById(R.id.food_menu_item_dialog_counter);

        itemSelectorView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < 1)
                    seekBar.setProgress(1);
                itemCounterView.setText(String.valueOf(i));
                foodCount = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setView(view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.add_to_cart, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (table != 0) {
                    Cart.CartItem cartItem = new Cart.CartItem(foodId, foodCount, orderNote.getText().toString(), tableNo.getSelectedItem().toString());
                    db.collection("carts")
                            .document(auth.getUid())
                            .update("items", FieldValue.arrayUnion(cartItem))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(view, R.string.cart_added, BaseTransientBottomBar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(view, task.getException().getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    tableLabel.setTextColor(getResources().getColor(R.color.errorTextColor));
                }
            }
        });
        return builder.create();
    }
}
