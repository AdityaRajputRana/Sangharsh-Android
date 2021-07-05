package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


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
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
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

    private void takeScreenshot() {
        Log.i("Screenshot", "starting");
        Date now = new Date();
        String date = (String) android.text.format.DateFormat.format("yyyy-MM-dd_hh.mm.ss", now);

        try {
            TextureView textureView = (TextureView) playerView.getVideoSurfaceView();
            Bitmap bitmap = textureView.getBitmap();
            Uri mImg;

            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG"+ date + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Sangharsh/");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                mImg = imageUri;
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            } else {
                File pix = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File myDir = new File(pix + "/Sangharsh/");
                if (!myDir.exists()){
                    myDir.mkdirs();
                }
                File imageFile = new File(myDir, "IMG"+date+".jpg");
                fos = new FileOutputStream(imageFile);
                mImg = Uri.fromFile(imageFile);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Objects.requireNonNull(fos).close();

            int quality = 100;
            showToast(bitmap, mImg);
            Log.i("Screenshot", "captured");
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
            Log.i("Screenshot", "failed");

        }
    }

    private void showToast(Bitmap bitmap, Uri uri) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.image_toast,
                (ViewGroup)findViewById(R.id.relativeLayout1));

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScreenshot(uri);
                Log.i("Screenshot", "toast clicked");
            }
        });
        Toast toast = new Toast(this);
        toast.setView(view);
        toast.show();
        Log.i("Screenshot", "toast shown");
    }

    private void openScreenshot(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
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

        playerView.findViewById(R.id.cross_im).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.stop();
                PlayerActivity.this.finish();
            }
        });

        playerView.findViewById(R.id.screen_shot)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!player.isLoading()){
                            takeScreenshot();
                        } else {
                            Toast.makeText(PlayerActivity.this, "Let the video load before capturing the screenshot", Toast.LENGTH_SHORT).show();
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
