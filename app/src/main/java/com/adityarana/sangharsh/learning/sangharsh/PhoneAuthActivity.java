package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Model.Referral;
import com.adityarana.sangharsh.learning.sangharsh.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private TextView infoTxt;
    private Button continueBtn;
    private Button skipBtn;
    private EditText phoneEditTxt;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private boolean isOtpSend = false;
    private boolean referralUserAdded = false;
    private String mobileNum;
    private String mVerificationID;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    private Boolean isNewUserRegistered = false;
    private boolean referralAwarded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        skipBtn = findViewById(R.id.skipBtn);

        TextView ppTxt = findViewById(R.id.ppTxt);
        ppTxt.setText(Html.fromHtml("By logging in you agree to our <font color=#2948FF>Terms and Conditions</font> and <font color=#2948FF> Privacy Policy </font>"));

        ppTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://sangharshapp.blogspot.com/p/privacy-policy_20.html?m=1"));
                startActivity(i);
            }
        });

        progressBar = findViewById(R.id.progressBar);
        infoTxt = findViewById(R.id.numberTxt);
        setPhoneLogin();
    }


    private void updateUI(FirebaseUser user) {
        if (user != null && isNewUserRegistered && referralAwarded && referralUserAdded) {
            setResult(RESULT_OK);
            finish();
        }
    }



    private void setPhoneLogin() {
        continueBtn = findViewById(R.id.continueBtn);
        phoneEditTxt = findViewById(R.id.editTextPhone);
        phoneEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int charLimit = 10;
                if (isOtpSend) {
                    charLimit = 6;
                }
                if (phoneEditTxt.getText().toString().length() != charLimit) {
                    continueBtn.setEnabled(false);
                    continueBtn.setBackground(PhoneAuthActivity.this.getResources().getDrawable(R.drawable.btn_primary_dis_bg));
                } else {
                    continueBtn.setBackground(PhoneAuthActivity.this.getResources().getDrawable(R.drawable.btn_primary_bg));
                    continueBtn.setEnabled(true);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneEditTxt.setEnabled(false);
                continueBtn.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (isOtpSend) {
                    verifyOtp();
                } else {
                    sendOpt();
                }
            }
        });
    }

    private void verifyOtp() {
        progressBar.setVisibility(View.VISIBLE);
        phoneEditTxt.setEnabled(false);
        continueBtn.setEnabled(false);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, phoneEditTxt.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void sendOpt() {
        mobileNum = phoneEditTxt.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobileNum,
                90,
                TimeUnit.SECONDS,
                PhoneAuthActivity.this,
                mCallBack
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkNewUSer(task);
                        } else {
                            Log.w("PhoneAuth", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                infoTxt.setText("Entered OTP was incorrect. Please Try again");
                                phoneEditTxt.setEnabled(true);
                                phoneEditTxt.setHint("Enter OTP");
                                infoTxt.setTextColor(getResources().getColor(R.color.red_color));
                                progressBar.setVisibility(View.INVISIBLE);
                            } else {
                                infoTxt.setText("Verification Timed Out. Enter Your Mobile number again");
                                phoneEditTxt.setEnabled(true);
                                phoneEditTxt.setHint("Enter OTP");
                                phoneEditTxt.setInputType(InputType.TYPE_CLASS_PHONE);
                                isOtpSend = false;
                            }
                        }
                    }
                });
    }

    private void checkNewUSer(Task<AuthResult> task) {
        FirebaseUser fuser = task.getResult().getUser();

        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
            User user = new User();
            showReferralEditText();
            user.setUid(task.getResult().getUser().getUid());
            String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            user.setLoginUUID(androidId);
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(fuser.getUid())
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            isNewUserRegistered = true;
                            updateUI(fuser);
                        }
                    });
        } else {
            isNewUserRegistered = true;
            referralAwarded = true;
            referralUserAdded = true;
            updateUI(fuser);
        }
    }

    private void showReferralEditText() {
        progressBar.setVisibility(View.GONE);
        continueBtn.setEnabled(true);
        phoneEditTxt.setText("");
        phoneEditTxt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        phoneEditTxt.setEnabled(true);
        infoTxt.setText("If you have any referral Code you may enter here (Optional). If not click the skip button");
        phoneEditTxt.setHint("Referral Code");

        Button skipBtn = findViewById(R.id.skipBtn);
        skipBtn.setVisibility(View.VISIBLE);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                referralAwarded = true;
                referralUserAdded = true;
                updateUI(FirebaseAuth.getInstance().getCurrentUser());
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneEditTxt.getText() != null && !phoneEditTxt.getText().toString().isEmpty()){
                    verifyReferralId();
                    disableEverything();
                }
            }
        });
    }

    private void verifyReferralId() {

        Log.i("MyLogs", "Verifying Now");

        FirebaseDatabase.getInstance()
                .getReference("referrals")
                .child(phoneEditTxt.getText().toString().toUpperCase())
                .child("exists")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null && snapshot.getValue(Boolean.class)){
                            startSignUp();
                        } else {
                            enableEverything();
                            phoneEditTxt.setError("This referral code does not exists");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        enableEverything();
                        Toast.makeText(PhoneAuthActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void startSignUp() {
        addReferral();
        addReferredUser();
    }

    private void addReferredUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        User mUser = new User();
        mUser.setUid(user.getUid());
        mUser.setReferredBy(phoneEditTxt.getText().toString().toUpperCase());
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
                        if (referralAwarded && isNewUserRegistered){
                            updateUI(user);
                        }
                    }
                });
    }


    private void addReferral() {
        FirebaseUser user = mAuth.getCurrentUser();

        Referral referral = new Referral();
        referral.setDetails(user.getDisplayName());
        referral.setPurchaseMade(false);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("timestamp", ServerValue.TIMESTAMP);
        referral.setJoinedOn(map);

        FirebaseDatabase.getInstance()
                .getReference("referrals")
                .child(phoneEditTxt.getText().toString().toUpperCase())
                .child(user.getUid())
                .setValue(referral)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        referralAwarded = true;
                        if (referralUserAdded && isNewUserRegistered){
                            updateUI(user);
                        }
                    }
                });
    }



    private void enableEverything() {
        progressBar.setVisibility(View.GONE);
        skipBtn.setEnabled(true);
        continueBtn.setEnabled(true);
        phoneEditTxt.setEnabled(true);
    }

 private void disableEverything() {
        progressBar.setVisibility(View.VISIBLE);
        skipBtn.setEnabled(false);
        continueBtn.setEnabled(false);
        phoneEditTxt.setEnabled(false);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneAuthActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            mVerificationID = s;
            mResendToken = forceResendingToken;

            progressBar.setVisibility(View.GONE);
            continueBtn.setEnabled(true);
            phoneEditTxt.setEnabled(true);
            infoTxt.setText("Enter the OTP sent to your mobile number " + phoneEditTxt.getText().toString());
            phoneEditTxt.setText("");
            phoneEditTxt.setHint("Enter OTP");
            isOtpSend = true;
        }
    };
}