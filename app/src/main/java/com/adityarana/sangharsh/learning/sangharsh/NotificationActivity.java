package com.adityarana.sangharsh.learning.sangharsh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adityarana.sangharsh.learning.sangharsh.Adapter.NotificationAdapter;
import com.adityarana.sangharsh.learning.sangharsh.Model.Notifications.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class NotificationActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ImageButton backBtn;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadData();
    }

    private void loadData() {
        FirebaseDatabase.getInstance().getReference("notifications")
                .child("all")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()  && snapshot.hasChildren()){
                            ArrayList<Notification> notifications = new ArrayList<Notification>();
                            for (DataSnapshot x : snapshot.getChildren()) {
                                Notification mNotif = x.getValue(Notification.class);
                                if (mNotif.getExpiresIn() != null && !mNotif.getExpiresIn().isEmpty() && mNotif.getTime() != null
                                        && mNotif.getTime().containsKey("createdAt")) {
                                    try {
                                        int expireIn = Integer.parseInt(mNotif.getExpiresIn());
                                        Date date = new Date();
                                        Date created = new Date();

                                        long diff = date.getTime() - created.getTime();
                                        int days = (int) (diff / (1000 * 60 * 60 * 24));

                                        if (days < expireIn) {
                                            notifications.add(mNotif);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        notifications.add(mNotif);
                                    }
                                } else {
                                    notifications.add(x.getValue(Notification.class));
                                }
                            }

                            if (notifications.size() >0){
                                Collections.reverse(notifications);
                                NotificationAdapter adapter = new NotificationAdapter(notifications);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.noNotificationTxt).setVisibility(View.VISIBLE);
                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(NotificationActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}