package com.adityarana.sangharsh.learning.sangharsh.Model;

import java.util.ArrayList;

public class HomeDocument {
    private ArrayList<Category> courses;


    public HomeDocument(ArrayList courses) {
        this.courses = courses;
    }

    public HomeDocument() {
    }

    public ArrayList<Category> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Category> courses) {
        this.courses = courses;
    }
}
