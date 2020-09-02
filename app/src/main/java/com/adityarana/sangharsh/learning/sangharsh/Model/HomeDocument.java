package com.adityarana.sangharsh.learning.sangharsh.Model;

import java.util.ArrayList;

public class HomeDocument {
    private ArrayList<HomeCategory> courses;


    public HomeDocument(ArrayList courses) {
        this.courses = courses;
    }

    public HomeDocument() {
    }

    public ArrayList<HomeCategory> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<HomeCategory> courses) {
        this.courses = courses;
    }
}
