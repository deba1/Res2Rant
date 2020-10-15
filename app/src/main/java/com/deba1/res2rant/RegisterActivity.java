package com.deba1.res2rant;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deba1.res2rant.models.UserRole;
import com.deba1.res2rant.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button registerButton;
    private EditText nameField, emailField, passwordField, mobileField, passwordField2;
    private ProgressBar loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.registerRegisterButton);
        nameField = findViewById(R.id.registerName);
        emailField = findViewById(R.id.registerEmail);
        passwordField = findViewById(R.id.registerPassword);
        mobileField = findViewById(R.id.registerMobile);
        passwordField2 = findViewById(R.id.registerPassword2);
        loading = findViewById(R.id.registerLoading);

        final TextView loginButton = findViewById(R.id.registerLoginText);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String name, email, password, password2, mobile;
                final int mobileNo;
                name = nameField.getText().toString().trim();
                email = emailField.getText().toString().trim();
                password = passwordField.getText().toString().trim();
                password2 = passwordField2.getText().toString().trim();
                mobile = mobileField.getText().toString().trim();

                if (!AppHelper.validateRegex(mobile, "^(?:\\+?88)?01[13-9]\\d{8}$")) {
                    mobileField.setError("Mobile No. is not valid!");
                }
                else if (!password.equals(password2)) {
                    passwordField2.setError(getResources().getString(R.string.repeat_pass_error));
                } else {
                    mobileNo = Integer.parseInt(mobile);
                    registerButton.setVisibility(View.INVISIBLE);
                    loading.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AuthResult authResult = task.getResult();
                                assert authResult != null;
                                FirebaseUser firebaseUser = authResult.getUser();
                                assert firebaseUser != null;
                                DocumentReference documentReference = db.collection("users").document(firebaseUser.getUid());
                                User user = new User();
                                user.email = email;
                                user.mobileNo = mobileNo;
                                user.name = name;
                                user.role = UserRole.CUSTOMER.toString();

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        loginIntent.putExtra("ref", "new_user");
                                        startActivity(loginIntent);
                                        finish();
                                    }
                                });

                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                loginIntent.putExtra("ref", "new_user_partial");
                                startActivity(loginIntent);
                                finish();
                            } else {
                                registerButton.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.INVISIBLE);
                                Snackbar.make(view, "Something went wrong!\nCause: " + task.getException().getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}