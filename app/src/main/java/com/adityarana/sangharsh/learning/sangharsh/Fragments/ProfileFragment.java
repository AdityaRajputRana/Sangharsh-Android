package com.adityarana.sangharsh.learning.sangharsh.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adityarana.sangharsh.learning.sangharsh.HelpActivity;
import com.adityarana.sangharsh.learning.sangharsh.LoginActivity;
import com.adityarana.sangharsh.learning.sangharsh.PhoneAuthActivity;
import com.adityarana.sangharsh.learning.sangharsh.Model.HomeCategory;
import com.adityarana.sangharsh.learning.sangharsh.PurchasedActivity;
import com.adityarana.sangharsh.learning.sangharsh.R;
import com.adityarana.sangharsh.learning.sangharsh.ReferActivity;
import com.adityarana.sangharsh.learning.sangharsh.Tools.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;


public class ProfileFragment extends Fragment {
    private ArrayList<HomeCategory> purchasedCats;
    private ArrayList<String> purchasedCourses;

    private ArrayList<String> buttonNames = new ArrayList<>(Arrays.asList(
            "Purchased Courses",
            "Log out",
            "Doubts Section",
            "Refer and Earn",
            "Privacy Policy"
    ));

    private ArrayList<Integer> buttonIcons = new ArrayList<>(Arrays.asList(
            R.drawable.ic_outline_purchased_turned_in_24,
            R.drawable.ic_baseline_exit_to_app_24,
            R.drawable.ic_baseline_chat_24,
            R.drawable.ic_baseline_share_24,
            R.drawable.ic_outline_policy_24
    ));

    private LinearLayout mLinearLayout;
    private ImageView profileImageView;
    private TextView nameTextView;
    FirebaseAuth auth;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mLinearLayout = view.findViewById(R.id.mLinearLayout);
        profileImageView = view.findViewById(R.id.profileImgView);
        nameTextView = view.findViewById(R.id.nameTextView);
        createButtonsCode();
        setDetailsCode();
        return view;
    }

    private void setDetailsCode() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user.getPhotoUrl() == null){
            profileImageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .transform(new CropCircleTransformation())
                    .into(profileImageView);
        }

        if (user.getDisplayName() == null || user.getDisplayName() == "" || user.getDisplayName().isEmpty()){
            nameTextView.setText(user.getPhoneNumber().substring(0, 3) + " " + user.getPhoneNumber().substring(3, 8)
            +  " " + user.getPhoneNumber().substring(8, 13));
        } else {
            nameTextView.setText(user.getDisplayName());
        }
    }

    private void createButtonsCode() {
        if (buttonNames != null && buttonNames.size() > 0){
            for (int i = 0; i<buttonNames.size(); i++){
                View layout = LayoutInflater.from(mLinearLayout.getContext()).inflate(R.layout.profile_view_item, mLinearLayout, false);
                TextView textView = layout.findViewById(R.id.btnNameTxt);
                ImageView imageView = layout.findViewById(R.id.iconImgView);
                imageView.setImageResource(buttonIcons.get(i));
                textView.setText(buttonNames.get(i));
                layout.setId(i);
                layout.setOnClickListener(clickListener);
                mLinearLayout.addView(layout);
            }
        }
    }

    private View.OnClickListener  clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case 0:
                    startPurchased();
                    break;
                case 1:
                    logOut();
                    break;
                case 2:
                    startMessageActivity();
                    break;
                case 3:
                    startReferralActivity();
                    break;
                case 4:
                    showPrivacyPolicy();
                    break;
                default:
                    break;
            }
        }
    };

    private void startReferralActivity() {
        startActivity(new Intent(getActivity(), ReferActivity.class));
    }

    private void showPrivacyPolicy() {
        Spanned text;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text = Html.fromHtml(new Constants().getPrivacyPolicyHTML(), Html.FROM_HTML_MODE_COMPACT);
        } else {
            text = Html.fromHtml(new Constants().getPrivacyPolicyHTML());
        }
        new AlertDialog.Builder(getActivity())
                .setTitle("Privacy Policy")
                .setMessage(text)
                .setCancelable(true)
                .show();

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

    private void startPurchased() {
        if(purchasedCats != null){
            Intent intent = new Intent(getActivity(), PurchasedActivity.class);
            intent.putExtra("PURCHASED_CATS", new Gson().toJson(purchasedCats));
            intent.putExtra("PURCHASED_COURSES", new Gson().toJson(purchasedCourses));
            startActivity(intent);
        }
    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Log out");
        builder.setMessage("Are you sure you want to log out? \n Your DOWNLOADS will be permanently DELETED.");
        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startLogOut();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    private void startLogOut() {
        auth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().getSharedPreferences("VIDEO_PREF", Context.MODE_PRIVATE).edit().clear().apply();
        getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE).edit().clear().apply();
        startActivity(intent);
        getActivity().finish();
    }

    public void setPurchasedCourses(ArrayList<HomeCategory> categories, ArrayList<String> purchased){
        purchasedCats = new ArrayList<HomeCategory>();
        purchasedCourses = purchased;
        for (HomeCategory category: categories) {
            if (purchased.contains(category.getId())){
                purchasedCats.add(category);
            }
        }
    }
}