package com.adityarana.sangharsh.learning.sangharsh.Model;

import java.util.HashMap;

public class Referral {
    String details;
    HashMap<String, Object> lastUpdates;
    Boolean purchaseMade;
    String firstPurchase;
    String firstOrderId;

    String uid;
    String referralId;
    String paymentNote;

    Boolean isRedeemed;
    Boolean isPaid;

    public Boolean getRedeemed() {
        return isRedeemed;
    }

    public void setRedeemed(Boolean redeemed) {
        isRedeemed = redeemed;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Referral(String details, HashMap<String, Object> lastUpdates, Boolean purchaseMade, String firstPurchase, String firstOrderId, String uid, String referralId, String paymentNote, Boolean isRedeemed, Boolean isPaid, String name) {
        this.details = details;
        this.lastUpdates = lastUpdates;
        this.purchaseMade = purchaseMade;
        this.firstPurchase = firstPurchase;
        this.firstOrderId = firstOrderId;
        this.uid = uid;
        this.referralId = referralId;
        this.paymentNote = paymentNote;
        this.isRedeemed = isRedeemed;
        this.isPaid = isPaid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public String getPaymentNote() {
        return paymentNote;
    }

    public void setPaymentNote(String paymentNote) {
        this.paymentNote = paymentNote;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public HashMap<String, Object> getLastUpdates() {
        return lastUpdates;
    }

    public void setLastUpdates(HashMap<String, Object> lastUpdates) {
        this.lastUpdates = lastUpdates;
    }

    public Boolean getPurchaseMade() {
        return purchaseMade;
    }

    public void setPurchaseMade(Boolean purchaseMade) {
        this.purchaseMade = purchaseMade;
    }

    public String getFirstPurchase() {
        return firstPurchase;
    }

    public void setFirstPurchase(String firstPurchase) {
        this.firstPurchase = firstPurchase;
    }

    public String getFirstOrderId() {
        return firstOrderId;
    }

    public void setFirstOrderId(String firstOrderId) {
        this.firstOrderId = firstOrderId;
    }

    public Referral() {
    }

    public Referral(String details, HashMap<String, Object> joinedOn, Boolean purchaseMade, String firstPurchase, String firstOrderId) {
        this.details = details;
        this.lastUpdates = joinedOn;
        this.purchaseMade = purchaseMade;
        this.firstPurchase = firstPurchase;
        this.firstOrderId = firstOrderId;
    }
}
