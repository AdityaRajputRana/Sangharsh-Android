package com.adityarana.sangharsh.learning.sangharsh.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adityarana.sangharsh.learning.sangharsh.R;
import com.squareup.picasso.Picasso;


public class BannerFragment extends Fragment {
    String url;
    String redirectUrl;
    String category;
    String subcategory;
    Listener listener;

    public BannerFragment() {
    }

    public interface Listener{
        void startCatActivity(String category);
    }

    public BannerFragment(String imgUrl, String redirectUrl, String category, String subcategory) {
        this.url = imgUrl;
        this.redirectUrl = redirectUrl;
        this.category = category;
        this.subcategory = subcategory;
        listener = (Listener) getActivity();
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.imageView);
        Picasso.get()
                .load(url)
                .into(imageView);

        if (category != null && !category.isEmpty()) {
            view.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        listener.startCatActivity(category);
                }
            });

            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                view.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(redirectUrl));
                        startActivity(i);
                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_banner, container, false);
        return view;
    }
}