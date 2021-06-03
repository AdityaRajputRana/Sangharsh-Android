package com.adityarana.sangharsh.learning.sangharsh.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.BannerPagerAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Adapter.HomeRecyclerViewAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Adapter.HomeViewPagerAdapter;
import com.adityarana.sangharsh.learning.sangharsh.CategoryActivity;
import com.adityarana.sangharsh.learning.sangharsh.HelpActivity;
import com.adityarana.sangharsh.learning.sangharsh.Model.Banner;
import com.adityarana.sangharsh.learning.sangharsh.Model.Category;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeDocument;
import com.adityarana.sangharsh.learning.sangharsh.NotificationActivity;
import com.adityarana.sangharsh.learning.sangharsh.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment implements HomeRecyclerViewAdapter.Listener {
    private RecyclerView mrecyclerView;
    private ProgressBar progressBar;
    private ViewPager bannerPager;
    private TextView counterTxt;
    private TextView availTxt;
    private ArrayList<Banner> banners;
    private ArrayList<String> purchased;
    private HomeRecyclerViewAdapter adapter;

    private LinearLayout buttonsPannel;

    private ArrayList<HomeCategory> purchasedCats;


    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public void updatePurchased(ArrayList<String> newPurchased){
        this.purchased = newPurchased;
        adapter.notifyDataSetChanged();
    }
    public void setHome(HomeDocument homeDocument, ArrayList<String> purchased){
        this.purchased = purchased;

        purchasedCats = new ArrayList<HomeCategory>();
        if (homeDocument != null && homeDocument.getCourses() != null && homeDocument.getCourses().size() > 0) {
            for (HomeCategory category : homeDocument.getCourses()) {
                if (purchased.contains(category.getId())) {
                    purchasedCats.add(category);
                }
            }
        }

        if (buttonsPannel != null){
            buttonsPannel.setVisibility(View.VISIBLE);
        }


        adapter = new HomeRecyclerViewAdapter(homeDocument.getCourses(), getActivity(),
                this, purchased);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mrecyclerView.setAdapter(adapter);
        mrecyclerView.setLayoutManager(layoutManager);
        mrecyclerView.setVisibility(View.VISIBLE);
        banners = homeDocument.getBanners();
        if (homeDocument.getBanners() != null && homeDocument.getBanners().size() > 0) {
            BannerPagerAdapter bannerAdapter = new BannerPagerAdapter(getChildFragmentManager(), homeDocument.getBanners(),
                    getActivity());
            bannerPager.setAdapter(bannerAdapter);
            bannerPager.setVisibility(View.VISIBLE);
            bannerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    setCounter(position);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            setCounter(0);
        }

        progressBar.setVisibility(View.GONE);
        availTxt.setVisibility(View.VISIBLE);
        if (homeDocument.getCourses().size() == 0){
            availTxt.setText("No courses available right now. Please visit again later!");
            availTxt.setTextSize(18);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mrecyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        bannerPager = view.findViewById(R.id.viewPager);
        counterTxt = (TextView) view.findViewById(R.id.counter);
        availTxt = (TextView) view.findViewById(R.id.availableTxt);

        view.findViewById(R.id.doubtBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (purchasedCats != null){
                    startMessageActivity();
                } else {
                    Toast.makeText(getActivity(), "Please wait while we load data,", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.notificationBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getActivity(), NotificationActivity.class));
            }
        });

        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        buttonsPannel = view.findViewById(R.id.buttonPanel);
        if (purchasedCats != null){
            buttonsPannel.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void startMessageActivity() {
        if (purchasedCats!= null && purchasedCats.size() > 0) {
            Intent intent = new Intent(getActivity(), HelpActivity.class);
            intent.putExtra("CHAT_ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
            intent.putExtra("NAME", "Sangharsh Support");
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No purchased courses found");
            builder.setMessage("Only the users which have purchased at least 1 course can seek help from us. This is to prevent bots and spammers from flooding our premium inbox.");
            builder.setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setCancelable(true);
            builder.show();
        }
    }


    @Override
    public void onClick(HomeCategory category) {
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("HOME_CATEGORY", new Gson().toJson(category));
        intent.putExtra("PURCHASED", purchased.contains(category.getId()));
        startActivity(intent);
    }

    private void setCounter(int count) {
        if (banners == null || banners.size() <=0) {
            bannerPager.setVisibility(View.GONE);
            counterTxt.setVisibility(View.GONE);
        } else {
            String html = "";
            for (int i = 0; i < banners.size(); i++) {
                if (i == count) {
                    html = html + " &#9679;";
                } else {
                    html = html + " &#9675;";
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                counterTxt.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
            } else {
                counterTxt.setText(Html.fromHtml(html));
            }
            if (bannerPager.getVisibility() == View.GONE){
                bannerPager.setVisibility(View.VISIBLE);
            }
            if (counterTxt.getVisibility() == View.GONE){
                counterTxt.setVisibility(View.VISIBLE);
            }
        }
    }
}