package com.deba1.res2rant.ui.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deba1.res2rant.R;
import com.deba1.res2rant.models.Cart;
import com.deba1.res2rant.models.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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
    private Context context;
    private String beforeText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public FoodItemDialog(Context context, Food food, Drawable foodImage) {
        this.context = context;
        this.foodId = food.id;
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
        Button plusButton = view.findViewById(R.id.food_qt_plus);
        Button minusButton = view.findViewById(R.id.food_qt_minus);
        final EditText qtField = view.findViewById(R.id.food_qt_number);
        qtField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int qt = Integer.parseInt(charSequence.toString());
                if (qt>15 || qt<1)
                    qtField.setText(beforeText);
                else {
                    foodCount = qt;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (foodCount<15)
                    foodCount++;
                qtField.setText(String.format("%s", foodCount));
            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (foodCount>1)
                    foodCount--;
                qtField.setText(String.format("%s", foodCount));
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
                    Cart.CartItem cartItem = new Cart.CartItem(foodId, foodName, foodCount, orderNote.getText().toString(), tableNo.getSelectedItem().toString());
                    db.collection("carts")
                            .document(auth.getUid())
                            .update("items", FieldValue.arrayUnion(cartItem))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, R.string.cart_added, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
