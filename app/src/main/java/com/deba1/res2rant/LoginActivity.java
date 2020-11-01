package com.deba1.res2rant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.deba1.res2rant.models.Cart;
import com.deba1.res2rant.models.User;
import com.deba1.res2rant.models.UserRole;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final PhoneAuthProvider sms = PhoneAuthProvider.getInstance();
    private EditText otpField, mobileField;
    private Button loginButton, sendOtpButton;
    private String mVerificationId;
    private ProgressBar progressBar;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private final long delay = 15000;
    private final Handler otpHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobileField = findViewById(R.id.login_mobile);
        sendOtpButton = findViewById(R.id.login_otp_button);
        otpField = findViewById(R.id.login_otp);
        progressBar = findViewById(R.id.loginProgressBar);
        loginButton = findViewById(R.id.loginButton);

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendOTP();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileField.getText().toString().isEmpty())
                    mobileField.setError("Mobile number is required");
                else if (otpField.getText().toString().isEmpty())
                    otpField.setError("OTP field can't be empty");
                else {
                    loginButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpField.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    public void redirectToDashboard(@Nullable User user) {
        assert user != null;
        if (user.role.equals(UserRole.CUSTOMER.name())) {
            Intent intent = new Intent(LoginActivity.this, CustomerActivity.class);
            intent.putExtra("name", user.name);
            intent.putExtra("email", user.mobileNo);
            checkUpdate();
            startActivity(intent);
            finish();
        }
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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

    private void SendOTP() {
        String mobileNo = mobileField.getText().toString();
        if (mobileNo.isEmpty())
            mobileField.setError("Mobile number is required!");
        else {
            sms.verifyPhoneNumber(
                    mobileNo,
                    60,
                    TimeUnit.SECONDS,
                    LoginActivity.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            mVerificationId = s;
                            sendOtpButton.setText(R.string.resend_otp);
                            sendOtpButton.setEnabled(false);
                            otpHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendOtpButton.setEnabled(true);
                                }
                            }, 15000);
                        }

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(getApplicationContext(), "Sending OTP failed! \nCause: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                            mVerificationId = s;
                        }
                    }
            );
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        final FirebaseUser firebaseUser = authResult.getUser();
                        assert firebaseUser != null;
                        AdditionalUserInfo userInfo = authResult.getAdditionalUserInfo();
                        assert userInfo != null;
                        if (userInfo.isNewUser()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle(R.string.register_full);
                            View alertView = View.inflate(LoginActivity.this, R.layout.dialog_user_register, null);
                            builder.setView(alertView);
                            builder.setCancelable(false);

                            final EditText fullName = alertView.findViewById(R.id.register_name);
                            Button registerButton = alertView.findViewById(R.id.register_button);
                            registerButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    User user = new User();
                                    user.mobileNo = firebaseUser.getPhoneNumber();
                                    user.name = fullName.getText().toString();
                                    user.role = UserRole.CUSTOMER.name();
                                    db.collection("users")
                                            .document(firebaseUser.getUid())
                                            .set(user);
                                    db.collection("carts")
                                            .document(firebaseUser.getUid())
                                            .set(new Cart());
                                    redirectToDashboard(user);
                                }
                            });
                            builder.show();
                        }
                        else {
                            db.collection("users")
                                    .document(firebaseUser.getUid())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot snapshot) {
                                            User user = new User(snapshot);
                                            redirectToDashboard(user);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, R.string.otp_fail, Toast.LENGTH_SHORT).show();
                    }
                });
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