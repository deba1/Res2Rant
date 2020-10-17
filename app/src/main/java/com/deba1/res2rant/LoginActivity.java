package com.deba1.res2rant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText emailField, passwordField;
    private ProgressBar progressBar;
    FirebaseUser user = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean newRegister = false;
        if (getIntent().hasExtra("ref"))
            newRegister = getIntent().getStringExtra("ref").equals("new_user");
        if (newRegister)
            Snackbar.make(findViewById(R.id.snackbar), R.string.register_success, BaseTransientBottomBar.LENGTH_SHORT).show();

        emailField = findViewById(R.id.editTextTextEmailAddress);
        passwordField = findViewById(R.id.editTextTextPassword);
        progressBar = findViewById(R.id.loginProgressBar);
        final Button loginButton = findViewById(R.id.loginButton);
        final TextView registerButton = findViewById(R.id.registerTextLink);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                loginButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(emailField.getText().toString().trim(), passwordField.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                user = authResult.getUser();
                                db.collection("users")
                                        .document(user.getUid())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                loginButton.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.GONE);
                                                String role = snapshot.getString("role");
                                                if (role==null) role = "";
                                                switch (role) {
                                                    case "ADMIN":
                                                        startActivity(new Intent(LoginActivity.this, AdminDashboard.class));
                                                        finish();
                                                        break;
                                                    case "CUSTOMER":
                                                        startActivity(new Intent(LoginActivity.this, CustomerActivity.class));
                                                        finish();
                                                        break;
                                                    default:
                                                        firebaseAuth.signOut();
                                                        Snackbar.make(view, "User not assigned to any role!", BaseTransientBottomBar.LENGTH_LONG).show();
                                                        loginButton.setVisibility(View.VISIBLE);
                                                        progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                firebaseAuth.signOut();
                                                Snackbar.make(view, "Something went wrong! "+e.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                                                loginButton.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(view, "Something went wrong! "+e.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                                loginButton.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }
}