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
    HashMap<String, Object> paymentDetails;
    HashMap<String, Object> redeemDetails;

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

    public HashMap<String, Object> getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(HashMap<String, Object> paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public HashMap<String, Object> getRedeemDetails() {
        return redeemDetails;
    }

    public void setRedeemDetails(HashMap<String, Object> redeemDetails) {
        this.redeemDetails = redeemDetails;
    }

    public Referral(String details, HashMap<String, Object> joinedOn, Boolean purchaseMade, String firstPurchase, String firstOrderId, String uid, String referralId, String paymentNote, HashMap<String, Object> paymentDetails, HashMap<String, Object> redeemDetails) {
        this.details = details;
        this.lastUpdates = joinedOn;
        this.purchaseMade = purchaseMade;
        this.firstPurchase = firstPurchase;
        this.firstOrderId = firstOrderId;
        this.uid = uid;
        this.referralId = referralId;
        this.paymentNote = paymentNote;
        this.paymentDetails = paymentDetails;
        this.redeemDetails = redeemDetails;
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
