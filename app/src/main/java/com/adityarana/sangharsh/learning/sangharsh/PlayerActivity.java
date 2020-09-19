package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.adityarana.sangharsh.learning.sangharsh.Model.Video;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;


public class PlayerActivity extends AppCompatActivity {
    private SharedPreferences myPref;
    private Video videoInfo;
    private ExoPlayer player;
    private PlayerView playerView;
    private String key = "_%66&";
    private long playbackPosition = 0;
    private Boolean playWhenReady = true;
    private int currentWindow;

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        videoInfo = new Gson().fromJson(getIntent().getStringExtra("VIDEO"),
                Video.class);
        myPref = this.getSharedPreferences("VIDEO_PREF", MODE_PRIVATE);
        if (myPref.getBoolean("is"+videoInfo.getId()+"Downloaded", false)){
            getDataFromLocal(videoInfo);
        } else {
            getDataFromDB(videoInfo);
        }

    }

    private void getDataFromLocal(Video videoInfo) {
        String path = myPref.getString(videoInfo.getId()+"AbsPath", null);
        if (path == null || path.isEmpty()){
            getDataFromDB(videoInfo);
        } else {
            initialisePlayer(path);
        }
    }

    private void getDataFromDB(Video videoInfo) {
        String category = videoInfo.getCategory();
        String subcat = videoInfo.getSubcat();
        String id = videoInfo.getId();
        String[] ids = id.split(key);
        FirebaseStorage.getInstance().getReference().
                child("content")
                .child("categories")
                .child(category)
                .child("subcat_"+subcat)
                .child(ids[1] + "." + ids[2])
                .getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            initialisePlayer(task.getResult().toString());
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    private void initialisePlayer(String url){
            playerView = findViewById(R.id.video_view);
            player = new SimpleExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            player.setPlayWhenReady(true);
        Uri uri = Uri.parse(url);
        MediaSource source = buildMediaSource(uri);
        player.prepare(source);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (player!=null) {
            player.setPlayWhenReady(true);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ProgressiveMediaSource.Factory(
                new DefaultDataSourceFactory(this, "exoplayer-sangharsh")
        ).createMediaSource(uri);
    }

    private void releasePlayer(){
            if (player != null) {
                playbackPosition = player.getCurrentPosition();
                currentWindow = player.getCurrentWindowIndex();
                playWhenReady = player.getPlayWhenReady();
                player.release();
                player = null;
            }
        }
    }
