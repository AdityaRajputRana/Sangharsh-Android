package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.CategoryRecyclerViewAdpter;
import com.adityarana.sangharsh.learning.sangharsh.Model.Category;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.SubCategory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class CategoryActivity extends AppCompatActivity implements CategoryRecyclerViewAdpter.Listener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    private HomeCategory homeCategory;
    private Category category;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        String strHome = getIntent().getStringExtra("HOME_CATEGORY");
        textView = findViewById(R.id.contentSoonText);
        progressBar = findViewById(R.id.progressBar);
        if (strHome.isEmpty()){
            progressBar.setVisibility(View.GONE);
            textView.setText("Some error occured");
        } else {
            homeCategory = new Gson().fromJson(strHome, HomeCategory.class);
            if (homeCategory.getSubcat() <= 0){
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                TextView titleTxt = findViewById(R.id.titleTxt);
                titleTxt.setText(homeCategory.getName());
                requestData();
            }
        }
    }

    private void requestData() {
        Log.i("Get Data", "Requesting Data");
        Log.i("Get Data:ID", homeCategory.getId());
        FirebaseFirestore.getInstance()
                .collection("Categories")
                .document(homeCategory.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                Log.i("GetData", task.getResult().toString());
                                category = task.getResult().toObject(Category.class);
                                setUpRecyclerView();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                textView.setText("Requested document is missing");
                                textView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            textView.setText("Data fecting error");
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        CategoryRecyclerViewAdpter adapter = new CategoryRecyclerViewAdpter(category.getSubcategories(), this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void openLectures(SubCategory subCategory) {
        Intent intent = new Intent(this, LecturesActivity.class);
        intent.putExtra("SUB_CATEGORY", new Gson().toJson(subCategory));
        startActivity(intent);
    }
}