package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Model.Referral;
import com.adityarana.sangharsh.learning.sangharsh.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText nameEt;
    private EditText emailEt;
    private EditText passEt;
    private EditText referralEt;
    private EditText confirmEt;
    private Button registerBtn;

    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passLayout;
    private TextInputLayout referralLayout;
    private TextInputLayout confirmLayout;

    private ProgressBar progressBar;

    private Boolean referralAwarded = false;
    private Boolean referralUserAdded = false;
    private Boolean userAddedToStore = false;
    private Boolean signedInAno = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
    }

    private void findViews() {
        nameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailET);
        passEt = findViewById(R.id.passwordET);
        confirmEt = findViewById(R.id.confirmPassEt);
        referralEt = findViewById(R.id.referralEt);
        registerBtn = findViewById(R.id.registerBtn);

        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passLayout = findViewById(R.id.passwordLayout);
        referralLayout = findViewById(R.id.referralLayout);
        confirmLayout = findViewById(R.id.confirmPasswordLayout);

        progressBar = findViewById(R.id.progressBar);

        SharedPreferences preferences = this.getSharedPreferences("MyPref", MODE_PRIVATE);
        String referredBy = preferences.getString("referredBy", null);

        mAuth = FirebaseAuth.getInstance();
        if (referredBy != null && !referredBy.isEmpty()){
            referralEt.setText(referredBy.toUpperCase());
            mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                signedInAno = true;
                            }
                        }
                    });
        }
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNullValues();
            }
        });
    }

    private void checkNullValues() {
        Boolean isEmpty = false;
        if (nameEt.getText() == null || nameEt.getText().toString().isEmpty()){
            isEmpty = true;
            nameLayout.setError("Name is required");
        }
        if (passEt.getText() == null || passEt.getText().toString().isEmpty()){
            isEmpty = true;
            passLayout.setError("Password is required");
        } else if (confirmEt.getText() == null || confirmEt.getText().toString().isEmpty() || !confirmEt.getText().toString().equals(passEt.getText().toString())){
            isEmpty = true;
            confirmLayout.setError("Passwords don't match");
        }
        if (emailEt.getText() == null || emailEt.getText().toString().isEmpty()){
            isEmpty = true;
            emailLayout.setError("Email is required");
        }

        if (!isEmpty){
            disableEverything();
            if (referralEt.getText() != null && !referralEt.getText().toString().isEmpty()){
                if (signedInAno){
                    verifyReferralId();
                } else {
                    mAuth.signInAnonymously()
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        signedInAno = true;
                                        verifyReferralId();
                                    }
                                }
                            });
                }
            } else {
                startSignUp();
            }
        }
    }

    private void startSignUp() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(emailEt.getText().toString(), passEt.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Sign in success, update UI with the signed-in user's information
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameEt.getText().toString()).build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("Auth", "User profile updated.");
                                                Log.d("Auth", "createUserWithEmail:success");
                                                if (referralEt.getText() == null || referralEt.getText().toString().isEmpty()){
                                                    User mUser = new User();
                                                    mUser.setUid(user.getUid());
                                                    String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                                    mUser.setLoginUUID(androidId);
                                                    FirebaseFirestore.getInstance()
                                                            .collection("Users")
                                                            .document(user.getUid())
                                                            .set(mUser)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    updateUI(user);
                                                                    userAddedToStore = true;
                                                                }
                                                            });
                                                } else {
                                                    addReferral();
                                                    addReferredUser();
                                                    addUserToStore();
                                                }
                                        }
                                    });

                           ;
                        } else {
                            // If sign in fails, display a message to the user.
                            enableEverything();
                            Log.w("Auth", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed. "+ task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void addUserToStore() {
        FirebaseUser user = mAuth.getCurrentUser();
        User mUser = new User();
        mUser.setUid(user.getUid());
        String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        mUser.setLoginUUID(androidId);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user.getUid())
                .set(mUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            userAddedToStore = true;
                            if (referralUserAdded && referralAwarded){
                                updateUI(user);
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            enableEverything();
                        }
                    }
                });
    }

    private void verifyReferralId() {

        Log.i("MyLogs", "Verifying Now");

        FirebaseDatabase.getInstance()
                .getReference("referrals")
                .child(referralEt.getText().toString().toUpperCase())
                .child("exists")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null && snapshot.getValue(Boolean.class)){
                            startSignUp();
                        } else {
                            enableEverything();
                            referralLayout.setError("This referral code does not exists");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        enableEverything();
                        Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addReferredUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        User mUser = new User();
        mUser.setUid(user.getUid());
        mUser.setReferredBy(referralEt.getText().toString().toUpperCase());
        String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        mUser.setLoginUUID(androidId);
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .setValue(mUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        referralUserAdded = true;
                        if (referralAwarded && userAddedToStore){
                            updateUI(user);
                        }
                    }
                });
    }
    private void addReferral() {
        FirebaseUser user = mAuth.getCurrentUser();

        Referral referral = new Referral();
        referral.setDetails(user.getDisplayName());
        referral.setName(user.getDisplayName());
        referral.setPurchaseMade(false);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("timestamp", ServerValue.TIMESTAMP);
        referral.setLastUpdates(map);

        FirebaseDatabase.getInstance()
                .getReference("referrals")
                .child(referralEt.getText().toString().toUpperCase())
                .child("referred")
                .child(user.getUid())
                .setValue(referral)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        referralAwarded = true;
                        if (referralUserAdded && userAddedToStore){
                            updateUI(user);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        this.getSharedPreferences("MyPref", MODE_PRIVATE).edit().remove("referredBy").apply();
        if (user != null) {
            if (user.isEmailVerified()) {
                setResult(RESULT_OK);
                finish();
            } else {
                user.sendEmailVerification();
                mAuth.signOut();
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("Verify your email")
                        .setMessage("In order to prevent spamming bots we have send a link to your email address. You have to click that link in" +
                                " order to verify your account. After verification you will be able to login")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.putExtra("Email", emailEt.getText().toString());
                                intent.putExtra("Pass", passEt.getText().toString());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }).show();
            }
        }
    }

    private void disableEverything() {
        registerBtn.setEnabled(false);
        confirmLayout.setEnabled(false);
        passLayout.setEnabled(false);
        emailLayout.setEnabled(false);
        nameLayout.setEnabled(false);
        referralLayout.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void enableEverything() {
        registerBtn.setEnabled(true);
        confirmLayout.setEnabled(true);
        passLayout.setEnabled(true);
        emailLayout.setEnabled(true);
        nameLayout.setEnabled(true);
        referralLayout.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }
}