package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button googleBtn;
    private Button phoneAuthBtn;

    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 101;

    private EditText emailEt;
    private EditText passEt;
    private TextInputLayout emailLayout;
    private TextInputLayout passLayout;
    private Button loginBtn;

    //for referral
    private TextView infoTxt;
    private Button continueBtn;
    private Button skipBtn;
    private EditText phoneEditTxt;

    private Boolean isNewUserRegistered = false;
    private boolean referralAwarded = false;
    private boolean referralUserAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);


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


        TextView registerTxt = findViewById(R.id.registerTxt);
        registerTxt.setText(Html.fromHtml("Don't have a account?<b> <font color=#2948FF>Register here</font> </b> "));


        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), 901);
            }
        });

        setGoogleLoginBtn();
        setGoogleLogin();
        setPhoneAuth();

        setEmailLogin();

        setReferral();
    }

    private void setReferral() {
        skipBtn = findViewById(R.id.skipBtn);
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
                int charLimit = 6;
                if (phoneEditTxt.getText().toString().length() != charLimit) {
                    continueBtn.setEnabled(false);
                    continueBtn.setBackground(LoginActivity.this.getResources().getDrawable(R.drawable.btn_primary_dis_bg));
                } else {
                    continueBtn.setBackground(LoginActivity.this.getResources().getDrawable(R.drawable.btn_primary_bg));
                    continueBtn.setEnabled(true);
                }
            }
        });

    }

    private void showReferralEditText() {
        enableEverything();
        continueBtn.setEnabled(false);
        findViewById(R.id.referralLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.mainLayout).setVisibility(View.GONE);

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
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
        referral.setLastUpdates(map);
        referral.setName(user.getDisplayName());

        FirebaseDatabase.getInstance()
                .getReference("referrals")
                .child(phoneEditTxt.getText().toString().toUpperCase())
                .child("referred")
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


    private void setEmailLogin() {
        emailEt = findViewById(R.id.emailET);
        passEt = findViewById(R.id.passwordET);
        emailLayout = findViewById(R.id.emailLayout);
        passLayout = findViewById(R.id.passwordLayout);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNullValues();
            }
        });
    }

    private void checkNullValues() {
        Boolean isEmpty = false;
        if (passEt.getText() == null || passEt.getText().toString().isEmpty()) {
            isEmpty = true;
            passLayout.setError("Password is required");
        }
        if (emailEt.getText() == null || emailEt.getText().toString().isEmpty()){
            isEmpty = true;
            emailLayout.setError("Email is required");
        }
        if (!isEmpty){
            disableEverything();
            startLoginFlow();
        }
    }

    private void disableEverything() {
        loginBtn.setEnabled(false);
        phoneAuthBtn.setEnabled(false);
        passLayout.setEnabled(false);
        emailLayout.setEnabled(false);
        googleBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        continueBtn.setEnabled(false);
        skipBtn.setEnabled(false);
        phoneEditTxt.setEnabled(false);
    }

    private void enableEverything() {
        loginBtn.setEnabled(true);
        phoneAuthBtn.setEnabled(true);
        passLayout.setEnabled(true);
        emailLayout.setEnabled(true);
        googleBtn.setEnabled(true);
        continueBtn.setEnabled(true);
        skipBtn.setEnabled(true);
        phoneEditTxt.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }


    private void startLoginFlow() {
        if (mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }

        mAuth.signInWithEmailAndPassword(emailEt.getText().toString(), passEt.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Auth", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            enableEverything();
                            Log.w("Auth", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void setPhoneAuth() {
        phoneAuthBtn = findViewById(R.id.phoneBtn);
        phoneAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, PhoneAuthActivity.class), 902);
            }
        });
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

    private void updateUI(FirebaseUser user) {
        Boolean isGoogleVerified = false;
        Boolean isEmailVerified = false;
        Boolean isPhoneVerified = false;
        if (user != null) {
            for (UserInfo x : user.getProviderData()) {
                if (x.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
                    isGoogleVerified = true;
                }
                if (x.getProviderId().equals(EmailAuthProvider.PROVIDER_ID)) {
                    if (user.isEmailVerified()) {
                        isEmailVerified = true;
                    }
                }
                if (x.getProviderId().equals(PhoneAuthProvider.PROVIDER_ID)) {
                    isPhoneVerified = true;
                }
            }
            if (isEmailVerified || isGoogleVerified || isPhoneVerified) {
                if ((isNewUserRegistered && referralAwarded && referralUserAdded) || isEmailVerified) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("isNewLogin", true);
                    startActivity(intent);
                    finish();
                }
            } else {
                mAuth.signOut();
                enableEverything();
                new AlertDialog.Builder(this)
                        .setCancelable(true)
                        .setTitle("Verify your email")
                        .setMessage("In order to prevent spamming bots we have send a link to your email address. You have to click that link in" +
                                " order to verify your account. After verification you will be able to login")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(FirebaseAuth.getInstance().getCurrentUser());
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

        if (requestCode == 902 && resultCode == RESULT_OK){
            isNewUserRegistered =true;
            referralAwarded = true;
            referralUserAdded = true;
            updateUI(FirebaseAuth.getInstance().getCurrentUser());
        }

        if (requestCode == 901 && resultCode == RESULT_OK && data != null){
            emailEt.setText(data.getStringExtra("Email"));
            passEt.setText(data.getStringExtra("Pass"));
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            checkNewUSer(task);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase Sign In", "signInWithCredential:failure", task.getException());
                            Snackbar.make((ConstraintLayout) findViewById(R.id.mainLayout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
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


}