package com.adityarana.sangharsh.learning.sangharsh.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adityarana.sangharsh.learning.sangharsh.Model.Referral;
import com.adityarana.sangharsh.learning.sangharsh.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.ReferViewHolder> {

    ArrayList<Referral> referrals;
    Context activity;

    public ReferralAdapter(HashMap<String, Referral> referrals, Context activity) {
        Collection<Referral> values = referrals.values();
        this.referrals = new ArrayList<Referral>(values);
        this.activity = activity;
    }


    @NonNull
    @Override
    public ReferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.refer_rv_item, parent, false);
        return new ReferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferViewHolder holder, int position) {
        holder.nameText.setText(referrals.get(position).getName());
        String details = "Your friends has not made his first payment yet";
        if (referrals.get(position).getPurchaseMade() != null && referrals.get(position).getPurchaseMade()){
            details = "This referral is available for redemption";
            holder.detailsTxt.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        }

        if (referrals.get(position).getRedeemed() != null && referrals.get(position).getRedeemed()){
            details = "Payment under processing. Our team is verifying all the transactions and payment shall be made as soon as possible";
            holder.detailsTxt.setTextColor(activity.getResources().getColor(R.color.blue_80));
        }

        if (referrals.get(position).getPaid() != null && referrals.get(position).getPaid()){
            details = "Payment done. You have earned the reward";
            holder.detailsTxt.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        }

        holder.detailsTxt.setText(details);
    }

    @Override
    public int getItemCount() {
        return referrals.size();
    }

    public class ReferViewHolder extends RecyclerView.ViewHolder{

        TextView nameText;
        TextView detailsTxt;
        public ReferViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.nameTxt);
            detailsTxt = itemView.findViewById(R.id.detailsTxt);
        }
    }
}
