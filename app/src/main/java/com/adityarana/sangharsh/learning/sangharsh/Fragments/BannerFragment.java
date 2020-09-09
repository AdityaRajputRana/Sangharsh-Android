package com.adityarana.sangharsh.learning.sangharsh.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adityarana.sangharsh.learning.sangharsh.R;
import com.squareup.picasso.Picasso;


public class BannerFragment extends Fragment {
    String url;

//    public void setImageView(String url) {
//        if (imageView != null) {
//            Log.i("ImageView", "Not null");
//            Picasso.get()
//                    .load(url)
//                    .placeholder(R.drawable.splash_bg)
//                    .into(imageView, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            Log.i("Info", "Loeaded");
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            Log.i("Info", "Failed: " + e.toString());
//                            e.printStackTrace();
//                        }
//                    });
//            Log.i("URl", url);
//        } else {
//            Log.i("ImageView", "Null");
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    setImageView(url);
//                    handler.removeCallbacksAndMessages(this);
//                }
//            }, 500);
//        }
//    }

    public BannerFragment(String imgUrl) {
        this.url = imgUrl;
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
                .transform(new RoundedCornersTransformation(50, 10))
                .into(imageView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_banner, container, false);
        return view;
    }
}