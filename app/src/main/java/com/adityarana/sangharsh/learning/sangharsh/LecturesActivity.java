package com.adityarana.sangharsh.learning.sangharsh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.LectureRecycleViewAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Model.SubCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.Topic;
import com.adityarana.sangharsh.learning.sangharsh.Model.Video;
import com.adityarana.sangharsh.learning.sangharsh.Tools.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import java.util.HashMap;

public class LecturesActivity extends AppCompatActivity implements LectureRecycleViewAdapter.Listener {

    RecyclerView recyclerView;
    LectureRecycleViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);
        TextView titleTxt = findViewById(R.id.titleTxt);
        recyclerView = findViewById(R.id.recyclerView);

        Boolean isPurchased = false;
        try {
            isPurchased = getIntent().getBooleanExtra("PURCHASED", false);
        } catch (Exception e){
            e.printStackTrace();
        }

        Boolean isTopic = getIntent().getBooleanExtra("IS_TOPIC", false);

        if (isTopic){
            String topicStr = getIntent().getStringExtra("TOPIC");
            if (topicStr != null) {
                Topic topic = new Gson().fromJson(topicStr, Topic.class);
                if (topic.getLectures() >= 0) {
                    titleTxt.setText(topic.getName());
                    try {
                        adapter = new LectureRecycleViewAdapter(topic.getVideos(), this,
                                isPurchased, this);
                        LinearLayoutManager manager = new LinearLayoutManager(this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(manager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    titleTxt.setText("We will be uploading lectures soon");
                }
            } else {
                titleTxt.setText("Some Error Occured");
            }
        } else {
            String subCategoryStr = getIntent().getStringExtra("SUB_CATEGORY");
            if (subCategoryStr != null) {
                SubCategory subCategory = new Gson().fromJson(subCategoryStr, SubCategory.class);
                if (subCategory.getLectures() >= 0) {
                    titleTxt.setText(subCategory.getName());
                    try {
                        adapter = new LectureRecycleViewAdapter(subCategory.getVideos(), this,
                                isPurchased, this);
                        LinearLayoutManager manager = new LinearLayoutManager(this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(manager);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    titleTxt.setText("We will be uploading lectures soon");
                }
            } else {
                titleTxt.setText("Some Error Occured");
            }
        }
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        }

    @Override
    public void playVideo(Video video) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("VIDEO", new Gson().toJson(video));
        startActivity(intent);
    }

    @Override
    public void promptToBuy() {
        Toast.makeText(this, "Buy this course to access the locked Videos!", Toast.LENGTH_LONG).show();
    }

}