package com.adityarana.sangharsh.learning.sangharsh.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adityarana.sangharsh.learning.sangharsh.Model.Video;
import com.adityarana.sangharsh.learning.sangharsh.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LectureRecycleViewAdapter extends RecyclerView.Adapter<LectureRecycleViewAdapter.LectureViewHolder> {

    private ArrayList<Video> videos;

    public LectureRecycleViewAdapter(ArrayList<Video> videos) {
        this.videos = videos;
    }

    @NonNull
    @Override
    public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LectureViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lecture_recycler_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LectureViewHolder holder, int position) {
        holder.indexText.setText(Integer.toString(position + 1));
        holder.titleTxt.setText(videos.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class LectureViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        TextView indexText;

        public LectureViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            indexText = itemView.findViewById(R.id.indexText);
        }
    }
}
