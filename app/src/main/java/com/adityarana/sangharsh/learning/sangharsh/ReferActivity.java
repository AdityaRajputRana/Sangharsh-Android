package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.ReferralAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Model.PaymentDetails;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

public class ReferActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private LinearLayout referLayout;
    private RecyclerView recyclerView;
    private LinearLayout processLayout;

    private TextView processTxt;
    private TextView codeTxt;
    private Button shareBtn;

    private String referId;
    private int state = 0;

    private LinearLayout paymentsLayout;
    private NestedScrollView scrollView;

    private HashMap<String, Referral> mReferrals;

    private ProgressBar progressBar;
    private Button bankBtn;
    private Button upiBtn;
    private Button bankViewBtn;
    private Button upiViewBtn;

    private EditText nameEt;
    private EditText upiEt;
    private EditText ifscEt;
    private EditText accountNum;

    private ProgressBar processBar;

    @Override
    public void onBackPressed() {
        switch (state){
            case 0:
                super.onBackPressed();
                break;
            case 1:
                scrollView.setVisibility(View.VISIBLE);
                paymentsLayout.setVisibility(View.GONE);
                state--;
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

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
        referLayout.setVisibility(View.VISIBLE);

        if (!isFirst) {
            loadReferredDetails();
        } else {
            processBar = findViewById(R.id.progressBar);
            processBar.setVisibility(View.GONE);
            processTxt.setText("Now share your code with your friends.\nList of users joined with your code will appear here.");
        }
    }

    private void loadReferredDetails() {
        processLayout.setVisibility(View.VISIBLE);
        processTxt.setText("Loading the list of accounts you referred");
        FirebaseDatabase.getInstance().getReference("referrals")
        .child(referId)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null){
                    boolean toShow = true;
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
                        } else {
                            processBar = findViewById(R.id.progressBar);
                            processBar.setVisibility(View.GONE);
                            processTxt.setText("List of users joined with your code will appear here. Share your code now by pressing the above button.");
                        }
                    } else {
                        processBar = findViewById(R.id.progressBar);
                        processBar.setVisibility(View.GONE);
                        processTxt.setText("List of users joined with your code will appear here. Share your code now by pressing the above button.");
                    }
                } else {
                    Log.i("MyLogs", "List is null");
                    processBar = findViewById(R.id.progressBar);
                    processBar.setVisibility(View.GONE);
                    processTxt.setText("Something went wrong while talking to the servers");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                processLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showRedeemBtn() {
        processLayout.setVisibility(View.GONE);

        scrollView = findViewById(R.id.nestedScroll);
        paymentsLayout = findViewById(R.id.paymentsLayout);

        Button redeem = findViewById(R.id.redeemBtn);
        redeem.setVisibility(View.VISIBLE);
        redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.setVisibility(View.GONE);
                paymentsLayout.setVisibility(View.VISIBLE);
                state++;
                loadPayments();
            }
        });
    }

    private void loadPayments() {
        bankBtn = findViewById(R.id.bankBtn);
        upiBtn = findViewById(R.id.upiButton);
        bankViewBtn = findViewById(R.id.bankViewBtn);
        upiViewBtn = findViewById(R.id.upiViewBtn);

        nameEt = findViewById(R.id.bankName);
        upiEt = findViewById(R.id.upiId);
        ifscEt = findViewById(R.id.bankIfsc);
        accountNum = findViewById(R.id.bankAccount);

        progressBar = findViewById(R.id.myProgressBar);

        upiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upiEt.getText() == null || upiEt.getText().toString().isEmpty()){
                    upiEt.setError("This field is required");
                } else {
                    uploadUpi(upiEt.getText().toString());
                }
            }
        });

        bankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isAllFilled = true;
                if (nameEt.getText() == null || nameEt.getText().toString().isEmpty()){
                    isAllFilled = false;
                    nameEt.setError("This field is required");
                }

                if (ifscEt.getText() == null || ifscEt.getText().toString().isEmpty()){
                    isAllFilled = false;
                    ifscEt.setError("This field is required");
                }

                if (accountNum.getText() == null || accountNum.getText().toString().isEmpty()){
                    isAllFilled = false;
                    accountNum.setError("This field is required");
                }

                if (isAllFilled){
                    uploadDataBank(nameEt.getText().toString(), accountNum.getText().toString(), ifscEt.getText().toString());
                }
            }
        });

        LinearLayout bankLayout = findViewById(R.id.bankLayout);
        LinearLayout upiLayout = findViewById(R.id.upiLayout);

        bankViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bankLayout.setVisibility(View.VISIBLE);
                upiLayout.setVisibility(View.GONE);
                bankViewBtn.setTextColor(ReferActivity.this.getResources().getColor(R.color.white));
                upiViewBtn.setTextColor(ReferActivity.this.getResources().getColor(R.color.colorPrimary));
                bankViewBtn.setBackground(ReferActivity.this.getResources().getDrawable(R.drawable.btn_primary_bg));
                upiViewBtn.setBackground(ReferActivity.this.getResources().getDrawable(R.drawable.btn_secondary_bg));
            }
        });

        upiViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upiLayout.setVisibility(View.VISIBLE);
                bankLayout.setVisibility(View.GONE);
                upiViewBtn.setTextColor(ReferActivity.this.getResources().getColor(R.color.white));
                bankViewBtn.setTextColor(ReferActivity.this.getResources().getColor(R.color.colorPrimary));
                upiViewBtn.setBackground(ReferActivity.this.getResources().getDrawable(R.drawable.btn_primary_bg));
                bankViewBtn.setBackground(ReferActivity.this.getResources().getDrawable(R.drawable.btn_secondary_bg));
            }
        });
    }

    private void uploadUpi(String upiId) {
        disable();
        PaymentDetails details = new PaymentDetails();
        details.setUpiId(upiId);
        details.setBankPayment(false);

        HashMap<String, Object> updates = new HashMap<>();
        Collection<Referral> collection = mReferrals.values();
        ArrayList<Referral> referrals = new ArrayList<Referral>(collection);
        for (Referral x: referrals){
            if (x.getPurchaseMade() != null && x.getPurchaseMade() && (x.getRedeemed() == null || !x.getRedeemed())){
                mReferrals.get(x.getUid()).setRedeemed(true);
                updates.put("referrals/" + x.getReferralId() + "/referred/" + x.getUid()
                        +"/redeemed", true);
            }
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.getDisplayName() != null){
            updates.put("admin/referrals/" + referId + "/name", user.getDisplayName());
        }

        if (user.getEmail() != null){
            updates.put("admin/referrals/" + referId + "/email", user.getEmail());

        }

        if (user.getPhoneNumber() != null){
            updates.put("admin/referrals/" + referId + "/phone", user.getPhoneNumber());

        }
        updates.put("admin/referrals/" + referId + "/id", referId);

        updates.put("referrals/" + referId+ "/paymentDetails", details);

        FirebaseDatabase.getInstance().getReference()
                .updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            enable();
                            showReferred(mReferrals);
                            onBackPressed();
                            Toast.makeText(ReferActivity.this, "We have registered your request. It will be processed as soon as possible", Toast.LENGTH_LONG).show();
                        } else {
                            enable();
                            Toast.makeText(ReferActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadDataBank(String name, String account, String ifsc) {
        disable();
        PaymentDetails details = new PaymentDetails();
        details.setAccountNumber(account);
        details.setHolderName(name);
        details.setIfscCode(ifsc);
        details.setBankPayment(true);

        HashMap<String, Object> updates = new HashMap<>();
        Collection<Referral> collection = mReferrals.values();
        ArrayList<Referral> referrals = new ArrayList<Referral>(collection);
        for (Referral x: referrals){
            if (x.getPurchaseMade() != null && x.getPurchaseMade() && (x.getRedeemed() == null || !x.getRedeemed())){
                mReferrals.get(x.getUid()).setRedeemed(true);
                updates.put("referrals/" + referId + "/referred/" + x.getUid()
                +"/redeemed", true);
            }
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.getDisplayName() != null){
            updates.put("admin/referrals/" + referId + "/name", user.getDisplayName());
        }

        if (user.getEmail() != null){
            updates.put("admin/referrals/" + referId + "/email", user.getEmail());

        }

        if (user.getPhoneNumber() != null){
            updates.put("admin/referrals/" + referId + "/phone", user.getPhoneNumber());

        }
        updates.put("admin/referrals/" + referId + "/id", referId);

        updates.put("referrals/" + referId+ "/paymentDetails", details);

        FirebaseDatabase.getInstance().getReference()
                .updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            enable();
                            showReferred(mReferrals);
                            onBackPressed();
                            Toast.makeText(ReferActivity.this, "We have registered your request. It will be processed as soon as possible", Toast.LENGTH_LONG).show();
                        } else {
                            enable();
                            Toast.makeText(ReferActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void disable() {
        progressBar.setVisibility(View.VISIBLE);
        bankBtn.setEnabled(false);
        upiBtn.setEnabled(false);
        bankViewBtn.setEnabled(false);
        upiViewBtn.setEnabled(false);
        ifscEt.setEnabled(false);
        nameEt.setEnabled(false);
        accountNum.setEnabled(false);
        upiEt.setEnabled(false);
    }
    private void enable() {
        progressBar.setVisibility(View.GONE);
        bankBtn.setEnabled(true);
        upiBtn.setEnabled(true);
        bankViewBtn.setEnabled(true);
        upiViewBtn.setEnabled(true);
        ifscEt.setEnabled(true);
        nameEt.setEnabled(true);
        accountNum.setEnabled(true);
        upiEt.setEnabled(true);
    }


    private void showReferred(HashMap<String, Referral> referrals){
        mReferrals = referrals;
        findViewById(R.id.referredDetailsLayout).setVisibility(View.VISIBLE);
        ReferralAdapter adapter = new ReferralAdapter(referrals, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        processLayout.setVisibility(View.GONE);
        processTxt.setText("");
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