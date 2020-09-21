package com.adityarana.sangharsh.learning.sangharsh.Model;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class User {
    String uid;
    ArrayList<String> purchasedCourses;

    public ArrayList<String> getPurchasedCourses() {
        return purchasedCourses;
    }

    public void setPurchasedCourses(ArrayList<String> purchasedCourses) {
        this.purchasedCourses = purchasedCourses;
    }

    public User(String uid, ArrayList<String> purchasedCourses) {
        this.uid = uid;
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
