package com.adityarana.sangharsh.learning.sangharsh.Adapter;

import android.content.Context;
import android.util.Log;

import com.adityarana.sangharsh.learning.sangharsh.Fragments.BannerFragment;
import com.adityarana.sangharsh.learning.sangharsh.Model.Banner;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class BannerPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Banner> banners;
    FragmentManager fragmentManager;

    public BannerPagerAdapter(@NonNull FragmentManager fm, ArrayList<Banner> banners, Context context) {
        super(fm);
        fragmentManager = fm;
        this.banners = banners;
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position < banners.size()){
            BannerFragment fragment = new BannerFragment(banners.get(position)
            .getImageUrl());
            if (!banners.get(position).getImageUrl().isEmpty()){
//                fragment.setImageView(banners.get(position).getImageUrl());
            }
            return fragment;
        } else {
            return null;
        }
    }

    public void addBanner(Banner newBanner){
        banners.add(newBanner);
    }
}
