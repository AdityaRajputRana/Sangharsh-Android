package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private TextView infoTxt;
    private Button continueBtn;
    private EditText phoneEditTxt;
    private Button googleBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private boolean isOtpSend = false;
    private String mobileNum;
    private String mVerificationID;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        infoTxt = findViewById(R.id.numberTxt);
        setPhoneLogin();
        setGoogleLoginBtn();
        setGoogleLogin();

    }

    private void setGoogleLogin() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void setGoogleLoginBtn() {
        googleBtn = findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        updateUI(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                progressBar.setVisibility(View.GONE);
                Log.w("Google Sign In Result", "Google sign in failed", e);
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (mAuth == null){mAuth = FirebaseAuth.getInstance();}

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase Sign In", "signInWithCredential:failure", task.getException());
                            Snackbar.make((ConstraintLayout)findViewById(R.id.mainLayout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
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
                if (isOtpSend){
                    charLimit = 6;
                }
                if(phoneEditTxt.getText().toString().length() != charLimit){
                    continueBtn.setEnabled(false);
                    continueBtn.setBackground(LoginActivity.this.getResources().getDrawable(R.drawable.btn_primary_dis_bg));
                } else {
                    continueBtn.setBackground(LoginActivity.this.getResources().getDrawable(R.drawable.btn_primary_bg));
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
                if (isOtpSend){
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
                LoginActivity.this,
                mCallBack
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if (mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("PhoneAuth", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
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

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LoginActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            mVerificationID = s;
            mResendToken = forceResendingToken;

            progressBar.setVisibility(View.GONE);
            continueBtn.setEnabled(true);
            phoneEditTxt.setText("");
            phoneEditTxt.setEnabled(true);
            infoTxt.setText("Enter the OTP sent to your mobile number " + phoneEditTxt.getText().toString());
            phoneEditTxt.setHint("Enter OTP");
            isOtpSend = true;
        }
    };
}