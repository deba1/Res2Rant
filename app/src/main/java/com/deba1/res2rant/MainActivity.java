package com.deba1.res2rant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.deba1.res2rant.models.User;
import com.deba1.res2rant.models.UserRole;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        if (firebaseAuth.getUid() == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }, 1500);
            finish();
        }
        else {
            db.collection("users")
                    .document(firebaseAuth.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            User user = snapshot.toObject(User.class);
                            assert user != null;
                            if (user.role.equals(UserRole.CUSTOMER.name())) {
                                Intent intent = new Intent(MainActivity.this, CustomerActivity.class);
                                intent.putExtra("name", user.name);
                                intent.putExtra("email", user.mobileNo);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                                alert.setTitle(R.string.customer_only);
                                alert.setMessage(R.string.customer_only_ex);
                                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        firebaseAuth.signOut();
                                        finish();
                                    }
                                });
                                alert.show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra("error", e.getMessage());
                            startActivity(intent);
                        }
                    });
        }
    }
}