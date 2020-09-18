package com.adityarana.sangharsh.learning.sangharsh.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<HomeCategory> categories;
    private ArrayList<String> purchasedCourses;
    private Listener listener;

    private int[] colorsLight;
    private int[] colorsDark;

    private int[] lockOpen;
    private int[] lockClosed;

    public HomeRecyclerViewAdapter(ArrayList<HomeCategory> categories, Context context, Listener listener, ArrayList<String> purchasedCourses) {
        this.categories = categories;
        this.listener = listener;
        this.purchasedCourses = purchasedCourses;
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

        this.lockOpen = new int[]{
                R.drawable.ic_open_lock_green,
                R.drawable.ic_open_lock_red,
                R.drawable.ic_open_lock_blue
        };

        this.lockClosed = new int[]{
                R.drawable.ic_locked_green,
                R.drawable.ic_locked_red,
                R.drawable.ic_locked_blue
        };
    }

    public interface Listener {
        void onClick(HomeCategory category);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_recyclew_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int colorIndex = position;
        while (colorIndex>2){
            colorIndex = colorIndex-3;
        }
        holder.cardView.setCardBackgroundColor(colorsLight[colorIndex]);
        final HomeCategory category = categories.get(position);
        holder.catName.setText(category.getName());
        holder.catName.setTextColor(colorsDark[colorIndex]);
        holder.subCat.setText(category.getSubcat() + " Sub-Categories");
        if (purchasedCourses.contains(category.getId())){
            holder.lockImg.setImageResource(lockOpen[colorIndex]);
        } else {
            holder.lockImg.setImageResource(lockClosed[colorIndex]);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (categories != null){
            return categories.size();
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
