package com.adityarana.sangharsh.learning.sangharsh.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.HomeRecyclerViewAdapter;
import com.adityarana.sangharsh.learning.sangharsh.CategoryActivity;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeDocument;
import com.adityarana.sangharsh.learning.sangharsh.R;
import com.google.gson.Gson;

public class HomeFragment extends Fragment implements HomeRecyclerViewAdapter.Listener {
    private RecyclerView mrecyclerView;
    private ProgressBar progressBar;
    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public void setHome(HomeDocument homeDocument){
        HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(homeDocument.getCourses(), getActivity(), this);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mrecyclerView.setAdapter(adapter);
        mrecyclerView.setLayoutManager(layoutManager);
        mrecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
        return view;
    }

    @Override
    public void onClick(HomeCategory category) {
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("HOME_CATEGORY", new Gson().toJson(category));
        startActivity(intent);
    }
}