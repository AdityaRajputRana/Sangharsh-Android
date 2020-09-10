package com.adityarana.sangharsh.learning.sangharsh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.HomeRecyclerViewAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class PurchasedActivity extends AppCompatActivity implements HomeRecyclerViewAdapter.Listener{
    private RecyclerView mrecyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased);

        mrecyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        //Getting Data
        ArrayList<HomeCategory> categories = new Gson().fromJson(getIntent().getStringExtra("PURCHASED_CATS"), new TypeToken<ArrayList<HomeCategory>>(){}.getType());
        ArrayList<String> purchased = new Gson().fromJson(getIntent().getStringExtra("PURCHASED_COURSES"), new TypeToken<ArrayList<String>>(){}.getType());
        //Setting RecyclerView
        Log.i("Categories", categories.toString());
        Log.i("Purchased", purchased.toString());
        HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(categories, this,
                this, purchased);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mrecyclerView.setAdapter(adapter);
        mrecyclerView.setLayoutManager(layoutManager);
        mrecyclerView.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(HomeCategory category) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("HOME_CATEGORY", new Gson().toJson(category));
        startActivity(intent);
    }
}