package com.adityarana.sangharsh.learning.sangharsh.Fragments;

import android.content.Context;
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
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeDocument;
import com.adityarana.sangharsh.learning.sangharsh.R;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public void setHome(HomeDocument homeDocument){
        Log.i("Get Data", "Setting Recycler View");
        HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(homeDocument.getCourses(), getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Log.i("Get Data", "Set Recycler View");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        return view;
    }

}