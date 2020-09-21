package com.adityarana.sangharsh.learning.sangharsh.Model;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class User {
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
