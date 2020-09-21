package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import me.ibrahimsn.lib.OnItemReselectedListener;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.HomeViewPagerAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Customs.HomeViewPager;
import com.adityarana.sangharsh.learning.sangharsh.Fragments.BannerFragment;
import com.adityarana.sangharsh.learning.sangharsh.Fragments.HomeFragment;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeDocument;
import com.adityarana.sangharsh.learning.sangharsh.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.razorpay.Checkout;

import java.util.ArrayList;

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

        Checkout.preload(getApplicationContext());
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
                        if (task.isSuccessful() && task.getResult().exists()) {
                            User mUser = task.getResult().toObject(User.class);
                            if (mUser.getPurchasedCourses() != null) {
                                Log.i("Purchased", "These are null");
                                purchased = mUser.getPurchasedCourses();
                            } else {
                                Log.i("Purchased", "These are not null" + String.valueOf(mUser.
                                        getPurchasedCourses().size()));
                                purchased = new ArrayList<String>();
                            }
                            if (homeDocument != null && toSetUp) {
                                setCourcesUI();
                            }
                        } else {
                            purchased = new ArrayList<String>();
                            Log.i("Error:Purchased ", String.valueOf(task.getException()));
                            if (homeDocument != null && toSetUp) {
                                setCourcesUI();
                            }
                        }
                    }
                });
    }

    private void setCourcesUI() {
        toSetUp = false;
        viewPagerAdapter.setHome(homeDocument, purchased);
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