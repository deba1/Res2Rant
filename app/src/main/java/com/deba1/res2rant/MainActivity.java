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

    private final Handler handler = new Handler();
    private Runnable runnable;
    private final long delay = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        OrderNotificationService.createNotificationChannel(this);

        if (firebaseAuth.getUid() == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                }
            }, 1500);
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
                                checkUpdate();
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(e.getMessage());
                            builder.setTitle("ERROR");
                            builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                        }
                    });
        }
    }

    private void checkUpdate() {
        final OrderNotificationService service = new OrderNotificationService(this);
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, delay);
                service.checkUpdate();
            }
        }, delay);
    }
}