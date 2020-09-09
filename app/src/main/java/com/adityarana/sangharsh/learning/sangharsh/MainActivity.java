package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import me.ibrahimsn.lib.OnItemReselectedListener;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.HomeViewPagerAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Customs.HomeViewPager;
import com.adityarana.sangharsh.learning.sangharsh.Fragments.HomeFragment;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeDocument;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    HomeViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViewPager();
        getData();
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
                       viewPagerAdapter.setHome(document.toObject(HomeDocument.class));
                    }
                } else {
                    Log.i("Get Data", "Data Fetching Error:" + task.getException().toString());
                }
            }
        });
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
}