package com.deba1.res2rant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.deba1.res2rant.models.User;
import com.deba1.res2rant.models.UserRole;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        if (firebaseAuth.getCurrentUser() == null) {
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
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                User user = task.getResult().toObject(User.class);
                                assert user != null;
                                switch (user.role) {
                                    case "ADMIN":
                                        startActivity(new Intent(MainActivity.this, AdminDashboard.class));
                                        break;
                                    case "CHEF":
                                        startActivity(new Intent(MainActivity.this, ChefActivity.class));
                                        break;
                                    case "CUSTOMER":
                                        startActivity(new Intent(MainActivity.this, CustomerActivity.class));
                                        break;
                                    default:
                                }
                                finish();
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.putExtra("error", task.getException().getMessage());
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
    }
}