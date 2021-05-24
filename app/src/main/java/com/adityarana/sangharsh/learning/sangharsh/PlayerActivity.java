package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adityarana.sangharsh.learning.sangharsh.Model.Video;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.ArrayList;


public class PlayerActivity extends AppCompatActivity {
    private SharedPreferences myPref;
    private Video videoInfo;
    private ExoPlayer player;
    private PlayerView playerView;
    private String key = "_%66&";
    private long playbackPosition = 0;
    private Boolean playWhenReady = true;
    private int currentWindow;

    private int speedIndex = 2;
    private ArrayList<TextView> speedBtns;
    private ConstraintLayout speedControls;
    private TextView speedText;
    private ArrayList<Float> speeds;

    private ProgressBar progressBar;

    private int state = 0;

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

        progressBar = findViewById(R.id.progressBar);


        videoInfo = new Gson().fromJson(getIntent().getStringExtra("VIDEO"),
                Video.class);
        myPref = this.getSharedPreferences("VIDEO_PREF", MODE_PRIVATE);
        if (myPref.getBoolean("is"+videoInfo.getId()+"Downloaded", false)){
            getDataFromLocal(videoInfo);
        } else {
            getDataFromDB(videoInfo);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
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

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState){
                    case Player.STATE_BUFFERING:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_READY:
                        progressBar.setVisibility(View.GONE);
                        break;
                }
            }
        });

        speedControls = findViewById(R.id.speedLayout);
        speedText = playerView.findViewById(R.id.speedTxt);

        speedBtns = new ArrayList<TextView>();

        speedBtns.add(findViewById(R.id.speed0));
        speedBtns.add(findViewById(R.id.speed1));
        speedBtns.add(findViewById(R.id.speed2));
        speedBtns.add(findViewById(R.id.speed3));
        speedBtns.add(findViewById(R.id.speed4));

        speeds = new ArrayList<Float>();
        speeds.add(0.25f);
        speeds.add(0.5f);
        speeds.add(1f);
        speeds.add(1.5f);
        speeds.add(2f);

        speedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speedControls.setVisibility(View.VISIBLE);
                state = 1;
            }
        });

        speedControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speedControls.setVisibility(View.GONE);
                state = 0;
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (state){
            case 0:
                super.onBackPressed();
                break;
            case 1:
                state = 0;
                speedControls.setVisibility(View.GONE);
                break;
        }
    }

    public void changeSpeed(View view){
        speedBtns.get(speedIndex).setTextColor(getResources().getColor(R.color.white));
        speedBtns.get(speedIndex).setBackgroundColor(getResources().getColor(R.color.transparent));

        speedIndex = Integer.valueOf(view.getTag().toString());

        speedBtns.get(speedIndex).setTextColor(getResources().getColor(R.color.gray_text));
        speedBtns.get(speedIndex).setBackgroundColor(getResources().getColor(R.color.white));

        speedText.setText(speedBtns.get(speedIndex).getText());
        speedControls.setVisibility(View.GONE);
        state = 0;

        PlaybackParameters parameters = new PlaybackParameters(speeds.get(speedIndex));
        player.setPlaybackParameters(parameters);
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
