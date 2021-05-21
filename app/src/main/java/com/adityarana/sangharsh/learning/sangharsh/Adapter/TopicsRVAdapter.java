package com.adityarana.sangharsh.learning.sangharsh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adityarana.sangharsh.learning.sangharsh.Model.SubCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.Topic;
import com.adityarana.sangharsh.learning.sangharsh.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class TopicsRVAdapter extends RecyclerView.Adapter<TopicsRVAdapter.MyViewHolder> {

    private ArrayList<Topic> topics;
    private Listener listener;

    private int[] colorsLight;
    private int[] colorsDark;

    private int[] next;


    public interface Listener{
        void openLectures(Topic topic);
    }

    public TopicsRVAdapter(ArrayList<Topic> topics, Context context, Listener listener) {
        this.topics = topics;
        this.listener = listener;

        this.colorsDark = new int[]{
                context.getResources().getColor(R.color.green_80),
                context.getResources().getColor(R.color.red_80),
                context.getResources().getColor(R.color.blue_80)
        };

        this.colorsLight = new int[]{
                context.getResources().getColor(R.color.green_20),
                context.getResources().getColor(R.color.red_20),
                context.getResources().getColor(R.color.blue_20)
        };

        this.next = new int[]{
                R.drawable.ic_next_green,
                R.drawable.ic_next_red,
                R.drawable.ic_next_blue
        };


    }

    @NonNull
    @Override
    public TopicsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_recyclew_view, parent, false);
        return new TopicsRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicsRVAdapter.MyViewHolder holder, int position) {
        final Topic topic = topics.get(position);
        holder.catName.setText(topic.getName());
        holder.subCat.setText(topic.getLectures() + " Lectures");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openLectures(topic);
            }
        });

        int colorIndex = position;
        while (colorIndex>2){
            colorIndex = colorIndex-3;
        }
        holder.cardView.setCardBackgroundColor(colorsLight[colorIndex]);
        holder.catName.setTextColor(colorsDark[colorIndex]);
        holder.lockImg.setImageResource(next[colorIndex]);
    }

    @Override
    public int getItemCount() {
        if (topics != null){
            return topics.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView catName;
        private TextView subCat;
        private ImageView lockImg;
        private CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            catName = itemView.findViewById(R.id.catName);
            subCat = itemView.findViewById(R.id.subCat);
            lockImg = itemView.findViewById(R.id.lockImg);
            cardView = itemView.findViewById(R.id.card);
        }
    }
}