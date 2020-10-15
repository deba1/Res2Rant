package com.deba1.res2rant.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.deba1.res2rant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private EditText nameField, mobileField;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Button editProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_profile, container, false);
        final String userId = auth.getUid();
        final FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        assert userId != null;

        // Assign Items
        nameField = mainView.findViewById(R.id.profile_name);
        mobileField = mainView.findViewById(R.id.profile_mobile);
        editProfile = mainView.findViewById(R.id.profile_edit_button);

        // Assign old values
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        nameField.setText(String.format("%s",snapshot.get("name")));
                        mobileField.setText(String.format("%s", snapshot.get("mobileNo")));
                        mobileField.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mainView, R.string.error_default, BaseTransientBottomBar.LENGTH_SHORT).show();
                        disableInputs();
                    }
                });

        // Edit Profile Button
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameField.getText().toString().isEmpty())
                {
                    nameField.setError(getResources().getString(R.string.error_empty));
                    nameField.requestFocus();
                }
                else
                db.collection("users")
                        .document(userId)
                        .update("name", nameField.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(mainView, R.string.profile_updated, BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(mainView, R.string.error_default, BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return mainView;
    }
    public void disableInputs() {
        nameField.setEnabled(false);
        editProfile.setEnabled(false);
    }
}