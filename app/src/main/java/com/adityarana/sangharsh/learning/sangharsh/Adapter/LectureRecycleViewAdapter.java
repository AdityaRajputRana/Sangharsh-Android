package com.adityarana.sangharsh.learning.sangharsh.Adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Model.Video;
import com.adityarana.sangharsh.learning.sangharsh.R;
import com.adityarana.sangharsh.learning.sangharsh.Tools.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.internal.Util;

public class LectureRecycleViewAdapter extends RecyclerView.Adapter<LectureRecycleViewAdapter.LectureViewHolder>
        implements Utils.Listener{

    private ArrayList<Video> videos;
    private Listener listener;
    private Boolean isPurchased;
    private Context activity;
    private HashMap<String, ImageView> hashMap;
    private HashMap<String, ProgressBar> progressBarHashMap;
    private HashMap<String, ProgressBar> indetHashMap;
    private HashMap<String, ImageView> lockImageHM;

    private View.OnClickListener mListener;

    @Override
    public void downloaded(Video video) {
        try {
            progressBarHashMap.get(video.getId()).setVisibility(View.GONE);
            progressBarHashMap.remove(video.getId());
            hashMap.remove(video.getId());
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(activity, "Please Restart App", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void changeProgress(int progress, Video video){
        if (progressBarHashMap.containsKey(video.getId())) {
            ProgressBar progressBar = progressBarHashMap.get(video.getId());
            if (progress >= 1) {
                if (progressBar.getVisibility() == View.GONE || progressBar.getVisibility() == View.INVISIBLE) {
                    progressBar.setVisibility(View.VISIBLE);
                    indetHashMap.get(video.getId()).setVisibility(View.GONE);
                    indetHashMap.remove(video.getId());
                    lockImageHM.get(video.getId()).setVisibility(View.VISIBLE);
                    lockImageHM.remove(video.getId());
                }
                progressBar.setProgress(progress);
            }
        }
    }



    public interface Listener{
        void playVideo(Video video);
        void promptToBuy();
    }

    public LectureRecycleViewAdapter(ArrayList<Video> videos, Listener listener, Boolean isPurchased, Context activity) {
        this.videos = videos;
        this.listener = listener;
        this.isPurchased = isPurchased;
        this.activity = activity;
        this.hashMap = new HashMap<String, ImageView>();
        this.progressBarHashMap = new HashMap<String, ProgressBar>();
        this.indetHashMap = new HashMap<String, ProgressBar>();
        this.lockImageHM = new HashMap<String, ImageView>();
    }

    @NonNull
    @Override
    public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LectureViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lecture_recycler_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LectureViewHolder holder, final int position) {
        holder.indexText.setText(Integer.toString(position + 1));
        holder.titleTxt.setText(videos.get(position).getName());
        if(isPurchased){
            Boolean isDownloaded = holder.itemView.getContext()
                    .getSharedPreferences("VIDEO_PREF", Context.MODE_PRIVATE)
                    .getBoolean("is"+ videos.get(position).getId()+"Downloaded", false);
            if (isDownloaded){
                holder.lockImgView.setImageResource(R.drawable.ic_round_delete_24);
                holder.lockImgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(activity)
                                .setTitle("Delete download")
                                .setMessage("Are you sure you want to delete "
                                + videos.get(position).getName() + "from your downloads?")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        new Utils().deleteFile(videos.get(position), activity);
                                        onBindViewHolder(holder, position);
                                    }
                                })
                                .setCancelable(true)
                                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                });
            } else {

                holder.lockImgView.setImageResource(R.drawable.ic_baseline_download_24);
                View.OnClickListener listener1 = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(activity)
                                .setTitle("Delete download")
                                .setMessage("Are you sure you want to delete "
                                        + videos.get(position).getName() + "from your downloads?")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        if (Utils.taskMap.containsKey(videos.get(position).getId())) {
                                            Objects.requireNonNull(Utils.taskMap.get(videos.get(position).getId())).cancel();
                                            Utils.taskMap.remove(videos.get(position).getId());
                                            Utils.tempFiles.remove(videos.get(position).getId());
                                            hashMap.remove(videos.get(position).getId());
                                            indetHashMap.remove(videos.get(position).getId());
                                            progressBarHashMap.remove(videos.get(position).getId());
                                            holder.progressBar.setVisibility(View.GONE);
                                            holder.lockImgView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_download_24));
                                            holder.lockImgView.setOnClickListener(mListener);
                                            holder.lockImgView.setVisibility(View.VISIBLE);
                                        } else {
                                            new Utils().deleteFile(videos.get(position), activity);
                                            holder.lockImgView.setOnClickListener(mListener);
                                            holder.lockImgView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_download_24));
                                        }
                                    }
                                })
                                .setCancelable(true)
                                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();

                    }
                };

                mListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startDownload(videos.get(position));

                        holder.lockImgView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_round_delete_24));

                        lockImageHM.put(videos.get(position).getId(), holder.lockImgView);
                        holder.lockImgView.setVisibility(View.GONE);
                        holder.lockImgView.setOnClickListener(listener1);
                        holder.progressBarIndet.setVisibility(View.VISIBLE);
                        hashMap.put(videos.get(position).getId(), holder.lockImgView);
                        progressBarHashMap.put(videos.get(position).getId(), holder.progressBar);
                        indetHashMap.put(videos.get(position).getId(), holder.progressBarIndet);
                    }
                };

                holder.lockImgView.setOnClickListener(mListener);

                holder.overlayBtn.setVisibility(View.VISIBLE);
                holder.overlayBtn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View view) {
                        if (Objects.requireNonNull(Utils.taskMap.get(videos.get(position).getId()).isPaused())){
                            Objects.requireNonNull(Utils.taskMap.get(videos.get(position).getId())).resume();

                            if (activity != null){
                                Toast.makeText(activity, "Download resumed!", Toast.LENGTH_SHORT).show();
                                holder.overlayBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_round_pause_24));
                            }

                        } else {
                            Objects.requireNonNull(Utils.taskMap.get(videos.get(position).getId())).pause();

                            if (activity != null){
                                Toast.makeText(activity, "Download paused!", Toast.LENGTH_SHORT).show();
                                holder.overlayBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_round_play_arrow_24));
                            }
                        }
                    }
                });

                if (Utils.taskMap != null && Utils.taskMap.containsKey(videos.get(position).getId())){
                    hashMap.put(videos.get(position).getId(), holder.lockImgView);
                    holder.lockImgView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_round_delete_24));
                    holder.lockImgView.setOnClickListener(listener1);
                    holder.progressBarIndet.setVisibility(View.VISIBLE);
                    hashMap.put(videos.get(position).getId(), holder.lockImgView);
                    progressBarHashMap.put(videos.get(position).getId(), holder.progressBar);

                    if (Objects.requireNonNull(Utils.taskMap.get(videos.get(position).getId())).getSnapshot().getTotalByteCount() != 0 && Utils.taskMap.get(videos.get(position).getId()).getSnapshot().getBytesTransferred()/Utils.taskMap.get(videos.get(position).getId()).getSnapshot().getTotalByteCount() <= 0.01){
                        lockImageHM.put(videos.get(position).getId(), holder.lockImgView);
                        holder.lockImgView.setVisibility(View.GONE);
                        indetHashMap.put(videos.get(position).getId(), holder.progressBarIndet);
                        holder.progressBarIndet.setVisibility(View.VISIBLE);
                    } else {
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.progressBarIndet.setVisibility(View.GONE);
                    }

                    resumeDownload(videos.get(position));

                    if (Utils.taskMap.get(videos.get(position).getId()).isPaused()){
                        holder.progressBarIndet.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        if (activity != null){
                            holder.overlayBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_round_play_arrow_24));
                        }
                    }
                }
            }
        }else if (videos.get(position).isSample()){
            holder.lockImgView.setImageResource(R.drawable.ic_open_lock_green);
        } else {
            holder.lockImgView.setImageResource(R.drawable.ic_locked_green);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPurchased || videos.get(position).isSample()){
                    listener.playVideo(videos.get(position));
                } else {
                    listener.promptToBuy();
                }
            }
        });
    }

    private void resumeDownload(Video video) {
        new Utils().setTask(video, activity, this);
    }

    private void startDownload(Video video) {
        new Utils().downloadVid(video, activity, this);
    }



    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class LectureViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        TextView indexText;
        ImageView lockImgView;
        ProgressBar progressBar;
        ProgressBar progressBarIndet;

        ImageView overlayBtn;

        public LectureViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            indexText = itemView.findViewById(R.id.indexText);
            lockImgView = itemView.findViewById(R.id.lockImgView);
            progressBar = itemView.findViewById(R.id.myProgressBar);
            progressBarIndet = itemView.findViewById(R.id.progressBarIndet);
            overlayBtn = itemView.findViewById(R.id.overlayImg);

        }
    }
}
