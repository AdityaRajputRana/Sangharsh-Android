package com.adityarana.sangharsh.learning.sangharsh.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.adityarana.sangharsh.learning.sangharsh.Model.SubCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.Video;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;


public class Utils {

    public  interface Listener{
        void downloaded(Video video);
    }


    public void downloadVid(Video videoInfo, Context context) {
        Log.i("Info", "Starting Download");
        try {

            File file = File.createTempFile("video", ".mp4");
            String key = "_%66&";
            String category = videoInfo.getCategory();
            String subcat = videoInfo.getSubcat();
            String id = videoInfo.getId();
            String[] ids = id.split(key);
            FirebaseStorage.getInstance().getReference().
                    child("content")
                    .child("categories")
                    .child(category)
                    .child("subcat_" + subcat)
                    .child(ids[1] + "." + ids[2])
                    .getFile(file)
                    .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.i("Download", String.valueOf(taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()) + "%");
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences preferences = context.getSharedPreferences("VIDEO_PREF", Context.MODE_PRIVATE);
                                preferences.edit().putBoolean("is"+videoInfo.getId()+"Downloaded", true)
                                        .putString(videoInfo.getId()+"Video", new Gson().toJson(videoInfo))
                                        .putString(videoInfo.getId()+"Path", file.getPath())
                                        .putString(videoInfo.getId()+"AbsPath", file.getAbsolutePath()).commit();
                                Listener listener = (Listener) context;
                                listener.downloaded(videoInfo);
                            }
                        }
                    });
            Log.i("Info", "Download Started");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
