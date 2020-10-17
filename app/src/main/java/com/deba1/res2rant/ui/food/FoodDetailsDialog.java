package com.deba1.res2rant.ui.food;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.deba1.res2rant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static android.app.Activity.RESULT_OK;

public class FoodDetailsDialog extends DialogFragment {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
    private String foodId, foodName, foodDesc;
    private float foodPrice;
    private ImageView foodImageView;
    private Drawable foodImage;
    private final int SELECT_FOOD_IMAGE = 10011;
    private View view;
    private Uri foodImageUri = null;

    public FoodDetailsDialog(String foodId, String foodName, String foodDesc, Drawable foodImage, float foodPrice) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodDesc = foodDesc;
        this.foodPrice = foodPrice;
        this.foodImage = foodImage;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.edit_food);
        view = getLayoutInflater().inflate(R.layout.food_details_dialog, null);
        EditText fieldName = view.findViewById(R.id.foodName);
        fieldName.setText(foodName);
        EditText fieldDesc = view.findViewById(R.id.foodDesc);
        fieldDesc.setText(foodDesc);
        EditText fieldPrice = view.findViewById(R.id.foodPrice);
        fieldPrice.setText(String.valueOf(foodPrice));
        foodImageView = view.findViewById(R.id.foodPreview);
        foodImageView.setImageDrawable(foodImage);
        Button foodImageSelectButton = view.findViewById(R.id.foodImageSelectButton);
        foodImageSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FOOD_IMAGE);
            }
        });
        builder.setView(view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseFirestore.collection("foods")
                        .document(foodId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(view, R.string.food_deleted, BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                foodName = ((EditText)view.findViewById(R.id.foodName)).getText().toString().trim();
                foodDesc = ((EditText)view.findViewById(R.id.foodDesc)).getText().toString().trim();
                foodPrice = Float.parseFloat(((EditText)view.findViewById(R.id.foodPrice)).getText().toString().trim());
                firebaseFirestore.collection("foods")
                        .document(foodId)
                        .update("name", foodName, "description", foodDesc, "price", foodPrice, "image", "foods/"+foodId)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (foodImageUri != null) {
                                    firebaseStorage.child("foods/"+foodId)
                                            .putFile(foodImageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Snackbar.make(view, R.string.image_upload_success, BaseTransientBottomBar.LENGTH_LONG)
                                                            .show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Snackbar.make(view, R.string.image_upload_fail, BaseTransientBottomBar.LENGTH_LONG)
                                                            .show();
                                                }
                                            });
                                }
                            }
                        });
            }
        });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_FOOD_IMAGE && resultCode == RESULT_OK) {
            foodImageUri = data.getData();
            foodImageView.setImageURI(foodImageUri);
        }
    }
}
