package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import me.ibrahimsn.lib.OnItemReselectedListener;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.HomeViewPagerAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Customs.HomeViewPager;
import com.adityarana.sangharsh.learning.sangharsh.Fragments.BannerFragment;
import com.adityarana.sangharsh.learning.sangharsh.Fragments.HomeFragment;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeDocument;
import com.adityarana.sangharsh.learning.sangharsh.Model.User;
import com.adityarana.sangharsh.learning.sangharsh.Tools.Utils;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.razorpay.Checkout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BannerFragment.Listener {

    HomeViewPagerAdapter viewPagerAdapter;
    HomeDocument homeDocument;
    Boolean toSetUp = true;
    ArrayList<String> purchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViewPager();
        getData();

        checkNewLogin();
        Checkout.preload(getApplicationContext());

        ((MyApp) this.getApplication()).startListening(this);
    }

    private void checkNewLogin() {
        if (getIntent().getBooleanExtra("isNewLogin", true)){
            String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("currentDevice")
                    .setValue(androidId);
            FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("devices")
                    .child(androidId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists() || snapshot.getValue() == null || snapshot.getValue(Boolean.class).equals(false)){
                                HashMap<String, Object> updates = new HashMap<String, Object>();
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("devices")
                                        .child(androidId)
                                        .setValue(true);
                               incrementNumDevices();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }

    private void incrementNumDevices() {
        FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("numDevices")
                .setValue(ServerValue.increment(1))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            checkForExcessDevices();
                        } else {
                            incrementNumDevices();
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkForExcessDevices() {
        FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("numDevices")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            if ( snapshot.getValue(Integer.class) > 4){
                                new AlertDialog.Builder(MainActivity.this)
                                        .setCancelable(true)
                                        .setTitle("Account is monitored")
                                        .setMessage("We have detected too many devices using this account. Hence we are constantly monitoring this account. " +
                                                "Note sharing the account and promoting piracy may lead to permanent termination of your account.\n If only you are using it on your own " +
                                                "devices then you need not to worry.")
                                        .setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getData() {
        FirebaseFirestore.getInstance().collection("app").document("Home")
        .get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        homeDocument = document.toObject(HomeDocument.class);
                        if (purchased != null && toSetUp) {
                            setCourcesUI();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error fetching the data " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.i("Get Data", "Data Fetching Error:" + task.getException().toString());
                }
            }
        });


        FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && Objects.requireNonNull(task.getResult()).exists()) {
                            User mUser = task.getResult().toObject(User.class);
                            if (mUser.getPurchasedCourses() != null) {
                                Log.i("Purchased", "These are null");
                                purchased = mUser.getPurchasedCourses();
                            } else {
                                purchased = new ArrayList<String>();
                            }
                            if (homeDocument != null && toSetUp) {
                                setCourcesUI();
                            }
                        } else {
                            purchased = new ArrayList<String>();
                            Log.i("Error:Purchased ", String.valueOf(task.getException()));
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (task.getException() != null){
                                Toast.makeText(MainActivity.this, "Something went wrong " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                Snackbar.make(MainActivity.this.getWindow().getDecorView().getRootView(), "Something went wrong:"+task.getException().getMessage(), BaseTransientBottomBar.LENGTH_INDEFINITE)
                                        .setAction("Report", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Utils.report(0, "Downloading user purchases",
                                                        task.getException(), user.getUid(), user.getEmail(), task.getException().getMessage());
                                            }
                                        });
                            } else if (task.getResult() == null || !task.getResult().exists()){
                                Utils.report(1, "Downloading user purchases",
                                        new Exception(), user.getUid(), user.getEmail(), "User data is not found");
                                Snackbar.make(MainActivity.this.getWindow().getDecorView().getRootView(), "Something went wrong:Your account specific data is missing on the server. Either create new accout data by clicking Create now, Or, you can contact us", BaseTransientBottomBar.LENGTH_INDEFINITE)
                                        .setAction("Create now", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                createNewAccountData();
                                            }
                                        })
                                        .setAction("Contact Us", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Utils.sendMail(MainActivity.this, "My data is not availabe", "Hello Sangharsh team, My data is missing on the servers\n Here " +
                                                        "are my details:" +
                                                        "UID:"+ user.getUid()
                                                + "Emails-Phone:" + user.getEmail() + user.getPhoneNumber());
                                            }
                                        });
                            }
                            Toast.makeText(MainActivity.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                            if (homeDocument != null && toSetUp) {
                                setCourcesUI();
                            }
                        }
                    }
                })
        .addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(MainActivity.this, "Request cancelled by the server.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewAccountData() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        User user = new User();
        user.setUid(fuser.getUid());
        String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        user.setLoginUUID(androidId);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(fuser.getUid())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Some error occured! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setCourcesUI() {
        toSetUp = false;
        viewPagerAdapter.setHome(homeDocument, purchased);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String cat  = MainActivity.this.getSharedPreferences("MY_PREF", MODE_PRIVATE).getString("PURCHASED_NOW", null);
        if (cat != null && !cat.isEmpty() && purchased != null && viewPagerAdapter != null){
            MainActivity.this.getSharedPreferences("MY_PREF", MODE_PRIVATE).edit()
                    .putString("PURCHASED_NOW", null).apply();
            purchased.add(cat);
            viewPagerAdapter.updatePurchased(purchased);
        }
    }

    private void setUpViewPager() {
        HomeViewPager viewPager;
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        SmoothBottomBar bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                if (viewPager.getCurrentItem() != i) {
                    viewPager.setCurrentItem(i);
                }
                return false;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomBar.setItemActiveIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void startCatActivity(String categoryId) {
        String category = "";
        for (HomeCategory homeCategory: homeDocument.getCourses()){
            if (homeCategory.getId() == categoryId){
                category = new Gson().toJson(homeCategory);
                break;
            }
        }
        if (category != null && !category.isEmpty()) {
            Intent intent = new Intent(this, CategoryActivity.class);
            intent.putExtra("HOME_CATEGORY", category);
            intent.putExtra("PURCHASED", purchased.contains(categoryId));
            startActivity(intent);
        }
    }
}