package com.adityarana.sangharsh.learning.sangharsh.Adapter;

import com.adityarana.sangharsh.learning.sangharsh.Fragments.HomeFragment;
import com.adityarana.sangharsh.learning.sangharsh.Fragments.ProfileFragment;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeDocument;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {
    HomeFragment homeFrag;
    ProfileFragment profileFrag;
    FragmentManager fragmentManager;

    public HomeViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if (homeFrag == null) homeFrag = new HomeFragment();
                return homeFrag;
            case 1:
                if (profileFrag == null) profileFrag = new ProfileFragment();
                return profileFrag;
            default:
                return null;
        }
    }



    @Override
    public int getCount() {
        return 2;
    }

    public void setHome(HomeDocument home, ArrayList<String> purchased){
        if (homeFrag == null) {homeFrag = new HomeFragment();}
        homeFrag.setHome(home, purchased);
        if (profileFrag == null){profileFrag = new ProfileFragment();}
        profileFrag.setPurchasedCourses(home.getCourses(), purchased);
    }

    public void updatePurchased( ArrayList<String> purchased){
        homeFrag.updatePurchased(purchased);
    }

}
