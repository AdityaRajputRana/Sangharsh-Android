package com.adityarana.sangharsh.learning.sangharsh.Model;

import java.util.ArrayList;

public class SubCategory {

    private String id, name;
    private int lectures;
    private ArrayList<Video> videos;

    public SubCategory(String id, String name, int lectures, ArrayList<Video> videos) {
        this.id = id;
        this.name = name;
        this.lectures = lectures;
        this.videos = videos;
    }

    public SubCategory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLectures() {
        return lectures;
    }

    public void setLectures(int lectures) {
        this.lectures = lectures;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }
}
