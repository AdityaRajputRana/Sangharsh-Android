package com.adityarana.sangharsh.learning.sangharsh.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Model.Category;
import com.adityarana.sangharsh.learning.sangharsh.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Category> categories;
    private Context context;

    public HomeRecyclerViewAdapter(ArrayList<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
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
        final Category category = categories.get(position);
        Log.i("Get Data", "Setting Views");

        holder.catName.setText(category.getName());
        holder.subCat.setText(category.getSubcat() + " Sub-Categories");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, category.getId() + " Card Clicked", Toast.LENGTH_LONG);
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
            Log.i("Get Data", "Setting ViewHolder");
            catName = itemView.findViewById(R.id.catName);
            subCat = itemView.findViewById(R.id.subCat);
            lockImg = itemView.findViewById(R.id.lockImg);
            cardView = itemView.findViewById(R.id.card);
        }
    }
}
