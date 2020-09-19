package com.adityarana.sangharsh.learning.sangharsh.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Model.Video;
import com.adityarana.sangharsh.learning.sangharsh.R;
import com.adityarana.sangharsh.learning.sangharsh.Tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LectureRecycleViewAdapter extends RecyclerView.Adapter<LectureRecycleViewAdapter.LectureViewHolder>
implements Utils.Listener{

    private ArrayList<Video> videos;
    private Listener listener;
    private Boolean isPurchased;
    private Context activity;
    private HashMap<String, ImageView> hashMap;

    @Override
    public void downloaded(Video video) {
        ImageView imageView = hashMap.get(video.getId());
        imageView.setImageResource(R.drawable.ic_baseline_done_24);
        hashMap.remove(video.getId());
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
                holder.lockImgView.setImageResource(R.drawable.ic_baseline_done_24);
            } else {
                holder.lockImgView.setImageResource(R.drawable.ic_baseline_download_24);
                holder.lockImgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startDownload(videos.get(position));
                        holder.lockImgView.setImageResource(R.drawable.ic_baseline_stop_24);
                        hashMap.put(videos.get(position).getId(), holder.lockImgView);
                    }
                });
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

    private void startDownload(Video video) {
        new Utils().downloadVid(video, activity);
    }



    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class LectureViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        TextView indexText;
        ImageView lockImgView;
        public LectureViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            indexText = itemView.findViewById(R.id.indexText);
            lockImgView = itemView.findViewById(R.id.lockImgView);
        }
    }
}
