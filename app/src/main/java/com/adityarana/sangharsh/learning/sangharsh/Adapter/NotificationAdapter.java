package com.adityarana.sangharsh.learning.sangharsh.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adityarana.sangharsh.learning.sangharsh.Model.Notifications.Notification;
import com.adityarana.sangharsh.learning.sangharsh.R;
import com.adityarana.sangharsh.learning.sangharsh.Tools.RoundedCornersTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private ArrayList<Notification> notifications;
    private Date date;
    private String userName;

    public NotificationAdapter(ArrayList<Notification> notifications) {
        this.notifications = notifications;
        date = new Date();
        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null &&
                !FirebaseAuth.getInstance().getCurrentUser().getDisplayName().isEmpty()) {
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        } else {
            userName = "user";
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notification mNotif = notifications.get(position);

        if (mNotif.getTitle() != null && !mNotif.getTitle().isEmpty()){
            mNotif.setTitle(mNotif.getTitle().replace("{user_name}", userName));
            holder.titleTxt.setText(mNotif.getTitle());
        } else {
            holder.titleTxt.setVisibility(View.GONE);
        }

        if (mNotif.getMessage() != null && !mNotif.getMessage().isEmpty()){
            mNotif.setMessage(mNotif.getMessage().replace("{user_name}", userName));
            holder.messageTxt.setText(mNotif.getMessage());
        } else {
            holder.messageTxt.setVisibility(View.GONE);
        }

        if (mNotif.getIcon() != null && !mNotif.getIcon().isEmpty()){
            Picasso.get()
                    .load(mNotif.getIcon())
                    .transform(new CropCircleTransformation())
                    .into(holder.icon);
        } else {
            Picasso.get()
                    .load(R.mipmap.ic_launcher)
                    .transform(new CropCircleTransformation())
                    .into(holder.icon);
        }

        if (mNotif.getImage() != null && !mNotif.getImage().isEmpty()){
            Picasso.get()
                    .load(mNotif.getImage())
                    .transform(new RoundedCornersTransformation(25, 20))
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        if (mNotif.getTitle() != null && mNotif.getTime().containsKey("createdAt")){
            Date createdAt = new Date((long) mNotif.getTime().get("createdAt"));

            long difference = date.getTime() -  createdAt.getTime();

            int seconds = (int) (difference/1000);
            int minutes = (int) (seconds/60);
            int hours = (int) (minutes/60);

            int days = (int) (hours/24);
            int months = (int) (days/30.5);
            int years = (int) (months/12);

            if (seconds<60){
                holder.timeTxt.setText(String.valueOf(seconds) + " sec");
            } else if (minutes<60){
                holder.timeTxt.setText(String.valueOf(minutes) + " min");
            } else if (hours<24){
                holder.timeTxt.setText(String.valueOf(hours) + "h");
            } else if (days<31){
                holder.timeTxt.setText(String.valueOf(days) + "d");
            } else if (months<12){
                holder.timeTxt.setText(String.valueOf(months) + "m" );
            } else {
                holder.timeTxt.setText(String.valueOf(years) + "y");
            }


        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt;
        TextView messageTxt;
        TextView timeTxt;
        ImageView icon;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTxt = itemView.findViewById(R.id.timeTxt);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            messageTxt = itemView.findViewById(R.id.messageTxt);
            icon = itemView.findViewById(R.id.icon);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
