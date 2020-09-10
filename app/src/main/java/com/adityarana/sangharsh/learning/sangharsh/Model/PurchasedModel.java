package com.adityarana.sangharsh.learning.sangharsh.Model;

import java.util.ArrayList;

public class PurchasedModel {
    private ArrayList<HomeCategory> purchasedCats;
    private ArrayList<String> purchasedCourses;

    public ArrayList<HomeCategory> getPurchasedCats() {
        return purchasedCats;
    }

    public void setPurchasedCats(ArrayList<HomeCategory> purchasedCats) {
        this.purchasedCats = purchasedCats;
    }

    public ArrayList<String> getPurchasedCourses() {
        return purchasedCourses;
    }

    public void setPurchasedCourses(ArrayList<String> purchasedCourses) {
        this.purchasedCourses = purchasedCourses;
    }

    public PurchasedModel() {
    }

    public PurchasedModel(ArrayList<HomeCategory> purchasedCats, ArrayList<String> purchasedCourses) {
        this.purchasedCats = purchasedCats;
        this.purchasedCourses = purchasedCourses;
    }
}
