package com.adityarana.sangharsh.learning.sangharsh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.CategoryRecyclerViewAdpter;
import com.adityarana.sangharsh.learning.sangharsh.Adapter.TopicsRVAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Model.Category;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.SubCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.Topic;
import com.adityarana.sangharsh.learning.sangharsh.Tools.OrderRepository;
import com.google.gson.Gson;

public class TopicsActivity extends AppCompatActivity implements TopicsRVAdapter.Listener{

    RecyclerView recyclerView;
    ProgressBar progressBar;
    private SubCategory subCategory;
    private TextView textView;
    private Button buyNowBtn;
    private OrderRepository orderRepository;
    private Boolean isPurchased;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        String strSuCat = getIntent().getStringExtra("SUB_CATEGORY");
        isPurchased = getIntent().getBooleanExtra("PURCHASED", false);

        textView = findViewById(R.id.contentSoonText);
        progressBar = findViewById(R.id.progressBar);

        if (strSuCat.isEmpty()){
            progressBar.setVisibility(View.GONE);
            textView.setText("Some error occured");
        } else {
            subCategory = new Gson().fromJson(strSuCat, SubCategory.class);
            if (subCategory.getTopics().size() <= 0){
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                TextView titleTxt = findViewById(R.id.titleTxt);
                titleTxt.setText(subCategory.getName());
                setUpRecyclerView();
            }
        }

    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        TopicsRVAdapter adapter = new TopicsRVAdapter(subCategory.getTopics(), this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void openLectures(Topic topic) {
        Intent intent = new Intent(this, LecturesActivity.class);
        intent.putExtra("IS_TOPIC", true);
        intent.putExtra("TOPIC", new Gson().toJson(topic));
        intent.putExtra("PURCHASED", isPurchased);
        startActivity(intent);
    }
}