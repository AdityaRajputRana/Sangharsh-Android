package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Tools.Utils;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    Boolean adInitialised = false;
    Boolean gotFirebaseData = false;
    Boolean isNewDevice;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                adInitialised = true;
                startActivity();
            }
        });

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            Log.i("Auth", user.getProviderData().get(0).getProviderId());
            Log.i("Auth", FirebaseAuthProvider.PROVIDER_ID);
            Boolean isReallyLoggedIn = false;
            for (UserInfo x : user.getProviderData()) {
                if (!x.getProviderId().equals(FirebaseAuthProvider.PROVIDER_ID)) {
                    isReallyLoggedIn = true;
                }
                }
            if (!isReallyLoggedIn){
                user.delete();
                FirebaseAuth.getInstance().signOut();
                user = null;
            }
        }

        checkForInternet();
        checkForAltLogin();
    }

    private void checkForInternet() {
        if (!Utils.isNetworkAvailable(this)) {
            Snackbar.make(SplashActivity.this.getWindow().getDecorView().getRootView(), "Please check your internet connection", BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkForAltLogin();
                            checkForInternet();
                        }
                    }).show();
        }
    }



    private void checkForAltLogin() {



        if (user != null) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("currentDevice")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot != null && !snapshot.getValue(String.class).isEmpty()) {
                                String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                if (snapshot.getValue(String.class).equals(androidId)) {
                                    isNewDevice = false;
                                } else {
                                    isNewDevice = true;
                                    FirebaseAuth.getInstance().signOut();
                                }
                            } else {
                                isNewDevice = true;
                            }
                            gotFirebaseData = true;
                            startActivity();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SplashActivity.this, "Error thrown", Toast.LENGTH_SHORT).show();
                            if (error.getCode() == DatabaseError.NETWORK_ERROR || error.getCode() == DatabaseError.DISCONNECTED) {
                                Snackbar.make(SplashActivity.this.getWindow().getDecorView().getRootView(), "Please check your internet connection", BaseTransientBottomBar.LENGTH_INDEFINITE)
                                        .setAction("Retry", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                checkForAltLogin();
                                            }
                                        }).show();
                            } else {
                                reportExceptions(error);
                                Snackbar.make(SplashActivity.this.getWindow().getDecorView().getRootView(),
                                        error.getMessage(), BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        checkForAltLogin();
                                    }
                                }).show();
                            }
                        }
                    });

        }
    }

    private void reportExceptions(DatabaseError error) {
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setUserId(user.getUid());
        crashlytics.setCustomKey("email", user.getEmail());
        crashlytics.setCustomKey("phone", user.getPhoneNumber());
        crashlytics.setCustomKey("error code", error.getCode());
        crashlytics.log("Was checking the other device login");
        crashlytics.log(error.getDetails());
        crashlytics.recordException(error.toException());
        crashlytics.sendUnsentReports();
    }

    private void startActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (adInitialised && (gotFirebaseData || user == null)){

            Intent intent;
            if (user != null && user.getUid() != null){
                intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("isNewLogin", isNewDevice);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }
    }

}