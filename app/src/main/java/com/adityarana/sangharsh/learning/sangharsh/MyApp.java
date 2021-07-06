package com.adityarana.sangharsh.learning.sangharsh;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.core.content.IntentCompat;

public class MyApp extends Application {

    public void startListening(Context context){
        Log.i("Events", "started Listening");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("currentDevice")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.i("Events", "Change detected");
                            if (snapshot.exists() && snapshot != null && !snapshot.getValue(String.class).isEmpty()) {
                                String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                                if (!snapshot.getValue(String.class).equals(androidId)) {
                                    Log.i("Events", "New log in detected");
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("isMsgIncluded", true)
                                            .putExtra("title", "New login detected")
                                            .putExtra("message", "We have detected a new login from a device, so we are " +
                                                    "logging you out here. Please note: Multiple login attempts may result in permanent ban of your account"
                                            );
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        }
    }
}