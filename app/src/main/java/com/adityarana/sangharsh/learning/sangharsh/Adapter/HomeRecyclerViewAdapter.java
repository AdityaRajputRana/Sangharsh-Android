package com.adityarana.sangharsh.learning.sangharsh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Model.Category;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<HomeCategory> categories;
    private Context context;
    private Listener listener;

    public HomeRecyclerViewAdapter(ArrayList<HomeCategory> categories, Context context, Listener listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
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
        final HomeCategory category = categories.get(position);
        holder.catName.setText(category.getName());
        holder.subCat.setText(category.getSubcat() + " Sub-Categories");
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
