package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.ReferralAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Model.Referral;
import com.adityarana.sangharsh.learning.sangharsh.Model.UserReferral;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Ref;
import java.util.HashMap;
import java.util.Random;

public class ReferActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private LinearLayout referLayout;
    private RecyclerView recyclerView;
    private LinearLayout processLayout;

    private TextView processTxt;
    private TextView codeTxt;
    private Button shareBtn;

    private String referId;
    private Button redeemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        findViews();
        loadReferId();
    }

    private void loadReferId() {
        SharedPreferences preferences = this.getSharedPreferences("MyPref", MODE_PRIVATE);
        referId = preferences.getString("referralId", null);
        if (referId == null || referId.isEmpty()){
            downloadReferId();
            processTxt.setText("Please wait while we fetch your code from server");
        } else {
            setUpViews();
        }
    }

    private void setUpViews(){
        setUpViews(false);
    }

    private void setUpViews(Boolean isFirst) {
        codeTxt.setText(referId);
        processLayout.setVisibility(View.GONE);
        referLayout.setVisibility(View.VISIBLE);

        if (!isFirst) {
            loadReferredDetails();
        }
    }

    private void loadReferredDetails() {
        FirebaseDatabase.getInstance().getReference("referrals")
        .child(referId)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null){
                    Boolean toShow = true;
                    if (snapshot.child("referred").exists() && snapshot.child("referred") != null) {
                        HashMap <String, Referral> map = new HashMap<String, Referral>();
                        for (DataSnapshot x : snapshot.child("referred").getChildren()){
                            Referral referral = x.getValue(Referral.class);
                            map.put(x.getKey(), referral);
                            if (toShow && referral.getPurchaseMade() != null && referral.getPurchaseMade()
                            && (referral.getRedeemed() == null || !referral.getRedeemed())){
                                showRedeemBtn();
                                toShow = false;
                            }
                        }
                        if (map.size() > 0) {
                            showReferred(map);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showRedeemBtn() {
    }

    private void showReferred(HashMap<String, Referral> referrals){
        findViewById(R.id.referredDetailsLayout).setVisibility(View.VISIBLE);
        ReferralAdapter adapter = new ReferralAdapter(referrals, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void downloadReferId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("referralId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null && !snapshot.getValue(String.class).isEmpty()){
                            ReferActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE).edit().putString("referralId", snapshot.getValue(String.class))
                                    .apply();
                            referId = snapshot.getValue(String.class);
                            setUpViews();
                        } else {
                            processTxt.setText("Looks like you have visited here for first time. We are generating you a new Referral Code");
                            generateReferralId();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void generateReferralId() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String firstPart = uid.substring(uid.length() -2, uid.length());
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(4);
        for(int i=0;i<4;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        String secondPart = sb.toString();

        String newReferralId = (firstPart + secondPart).toUpperCase();
        Log.i("Generated", newReferralId);

        FirebaseDatabase.getInstance().getReference("referrals")
                .child(newReferralId)
                .child("exists")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists() || snapshot.getValue() == null || !snapshot.getValue(Boolean.class)){
                            referId = newReferralId;
                            saveReferralId();
                        } else {
                            generateReferralId();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void saveReferralId() {
        UserReferral referral = new UserReferral();
        referral.setExists(true);
        referral.setReferralId(referId);
        referral.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("referrals/"+referId, referral);
        updates.put("users/"+ referral.getUid() +"/referralId", referId);

        FirebaseDatabase.getInstance()
                .getReference()
                .updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            ReferActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE).edit().putString("referralId", referId).apply();
                            setUpViews(true);
                        } else {
                            generateReferralId();
                        }
                    }
                });
    }

    private void findViews() {
        referLayout = findViewById(R.id.referralLayout);
        processLayout = findViewById(R.id.processLayout);

        processTxt = findViewById(R.id.processTxt);
        codeTxt = findViewById(R.id.codeTxt);

        shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                String shareBody = "Hello, I want you to try the Sangharsh learning app. It has tons of content that can help you with your boards as well as competitive examinations." +
                        "Download it for free from playstore " +
                        "https://play.google.com/store/apps/details?id=com.sangharsh.learning" +
                        "\nIf you are prompted then use this code to sign up: " + referId.toUpperCase()  ;
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download Sangharsh Learning app");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "share using"));
            }
        });

        recyclerView = findViewById(R.id.recyclerView);

    }
}