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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;


public class Utils {

    public static HashMap<String, StorageTask<FileDownloadTask.TaskSnapshot>> taskMap;
    public static HashMap<String, File> tempFiles;

    public interface Listener{
        void downloaded(Video video);
        void changeProgress(int progress, Video video);
    }

    public void setTask(Video videoInfo, Context context, Listener listener){
        File file = Utils.tempFiles.get(videoInfo.getId());
        Utils.taskMap.get(videoInfo.getId()).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    SharedPreferences preferences = context.getSharedPreferences("VIDEO_PREF", Context.MODE_PRIVATE);
                    preferences.edit().putBoolean("is"+videoInfo.getId()+"Downloaded", true)
                            .putString(videoInfo.getId()+"Video", new Gson().toJson(videoInfo))
                            .putString(videoInfo.getId()+"Path", file.getPath())
                            .putString(videoInfo.getId()+"AbsPath", file.getAbsolutePath()).commit();
                    Listener mListner = (Listener) listener;
                    mListner.downloaded(videoInfo);
                    taskMap.remove(videoInfo.getId());
                    tempFiles.remove(videoInfo.getId());
                }
            }
        })
        .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if (!taskMap.containsKey(videoInfo.getId())){
                        taskMap.put(videoInfo.getId(), taskSnapshot.getTask());
                        tempFiles.put(videoInfo.getId(), file);
                    }
                    Listener mListner = (Listener) listener;
                    mListner.changeProgress((int) (taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()), videoInfo);
                    Log.i("Download", String.valueOf(taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()) + "%");
                }
        });
    }

    public void downloadVid(Video videoInfo, Context context, Listener listener) {
        if (taskMap == null){
            taskMap = new HashMap<String,StorageTask<FileDownloadTask.TaskSnapshot>>();
            tempFiles = new HashMap<String, File>();
        }
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
                            if (!taskMap.containsKey(videoInfo.getId())){
                                taskMap.put(videoInfo.getId(), taskSnapshot.getTask());
                                tempFiles.put(videoInfo.getId(), file);
                            }
                            Listener mListner = (Listener) listener;
                            mListner.changeProgress((int) (taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount()), videoInfo);
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
                                Listener mListner = (Listener) listener;
                                mListner.downloaded(videoInfo);
                                taskMap.remove(videoInfo.getId());
                                tempFiles.remove(videoInfo.getId());
                            }
                        }
                    });
            Log.i("Info", "Download Started");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
