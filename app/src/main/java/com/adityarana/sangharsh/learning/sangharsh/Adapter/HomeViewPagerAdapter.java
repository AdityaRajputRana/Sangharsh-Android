package com.adityarana.sangharsh.learning.sangharsh.Adapter;

import com.adityarana.sangharsh.learning.sangharsh.Fragments.HomeFragment;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeDocument;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {
    HomeFragment homeFrag;
    Fragment profileFrag;

    public HomeViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if (homeFrag == null) homeFrag = new HomeFragment();
                return homeFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    public void setHome(HomeDocument home){
        if (homeFrag == null) homeFrag = new HomeFragment();
        homeFrag.setHome(home);
    }
}
