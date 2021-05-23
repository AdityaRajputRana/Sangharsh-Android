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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private TextView infoTxt;
    private Button continueBtn;
    private EditText phoneEditTxt;
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
        setContentView(R.layout.activity_phone_auth);


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
        if (user != null) {
            startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));
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
            user.setUid(task.getResult().getUser().getUid());
//            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

//            final String tmDevice, tmSerial, androidId;
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                tmDevice = "";
//                tmSerial = "";
//            } else {
//                tmDevice = "" + tm.getDeviceId();
//                tmSerial = "" + tm.getSimSerialNumber();
//            }
            String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            user.setLoginUUID(androidId);
//            UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(fuser.getUid())
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(fuser);
                        }
                    });
        } else {
            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(fuser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                User user = task.getResult().toObject(User.class);
                                String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                if (androidId.equals(user.getLoginUUID())) {
                                    updateUI(fuser);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PhoneAuthActivity.this);
                                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                            .setCancelable(true)
                                            .setTitle("Access Denied")
                                            .setMessage("You are on different device than that with which this account was registered. Please use that same phone or Contact Us")
                                            .show();
                                    FirebaseAuth.getInstance().signOut();
                                }
                            } else {
                                Toast.makeText(PhoneAuthActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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
            phoneEditTxt.setText("");
            phoneEditTxt.setEnabled(true);
            infoTxt.setText("Enter the OTP sent to your mobile number " + phoneEditTxt.getText().toString());
            phoneEditTxt.setHint("Enter OTP");
            isOtpSend = true;
        }
    };
}