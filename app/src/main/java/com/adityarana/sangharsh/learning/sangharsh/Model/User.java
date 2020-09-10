package com.adityarana.sangharsh.learning.sangharsh.Model;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class User {
    String name, photoUrl, uid;
    ArrayList<String> purchasedCourses;

    @PropertyName("purchasedCources")
    public ArrayList<String> getPurchasedCourses() {
        return purchasedCourses;
    }

    @PropertyName("purchasedCources")
    public void setPurchasedCourses(ArrayList<String> purchasedCourses) {
        this.purchasedCourses = purchasedCourses;
    }

    public User(String name, String photoUrl, String uid, ArrayList<String> purchasedCourses) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.uid = uid;
        this.purchasedCourses = purchasedCourses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
