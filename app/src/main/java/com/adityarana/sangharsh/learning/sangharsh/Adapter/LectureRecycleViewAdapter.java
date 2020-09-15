package com.adityarana.sangharsh.learning.sangharsh.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Model.Video;
import com.adityarana.sangharsh.learning.sangharsh.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LectureRecycleViewAdapter extends RecyclerView.Adapter<LectureRecycleViewAdapter.LectureViewHolder> {

    private ArrayList<Video> videos;
    private Listener listener;
    private Boolean isPurchased;

    public interface Listener{
        void playVideo(Video video);
        void promptToBuy();
    }

    public LectureRecycleViewAdapter(ArrayList<Video> videos, Listener listener, Boolean isPurchased) {
        this.videos = videos;
        this.listener = listener;
        this.isPurchased = isPurchased;
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
        if (isPurchased || videos.get(position).isSample()){
            holder.lockImgView.setImageResource(R.drawable.ic_baseline_lock_open_24);
        } else {
            holder.lockImgView.setImageResource(R.drawable.ic_baseline_lock_24);
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
