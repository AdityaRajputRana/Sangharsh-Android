package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    Boolean adInitialised = false;
    Boolean gotFirebaseData = false;
    Boolean isNewDevice;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("currentDevice")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot != null && !snapshot.getValue(String.class).isEmpty()) {
                                String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                if (snapshot.getValue(String.class).equals(androidId)){
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

                        }
                    });
        }
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