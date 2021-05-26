package com.adityarana.sangharsh.learning.sangharsh.Model;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class User {

    public User(String referralId, String referredBy, String uid, String loginUUID, ArrayList<String> purchasedCourses) {
        this.referralId = referralId;
        this.referredBy = referredBy;
        this.uid = uid;
        this.loginUUID = loginUUID;
        this.purchasedCourses = purchasedCourses;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    String referralId;
    String referredBy;
    String uid;

    public User(String uid, String loginUUID, ArrayList<String> purchasedCourses) {
        this.uid = uid;
        this.loginUUID = loginUUID;
        this.purchasedCourses = purchasedCourses;
    }

    public String getLoginUUID() {
        return loginUUID;
    }

    public void setLoginUUID(String loginUUID) {
        this.loginUUID = loginUUID;
    }

    String loginUUID;
    ArrayList<String> purchasedCourses;

    public ArrayList<String> getPurchasedCourses() {
        return purchasedCourses;
    }

    public void setPurchasedCourses(ArrayList<String> purchasedCourses) {
        this.purchasedCourses = purchasedCourses;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



    public User() {
    }

}
