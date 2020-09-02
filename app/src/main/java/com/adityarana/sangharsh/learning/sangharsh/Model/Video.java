package com.adityarana.sangharsh.learning.sangharsh.Model;

public class Video {
    private String id, name, description;
    private boolean sample;
    private int time;

    public Video(String id, String name, String description, boolean sample, int time) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sample = sample;
        this.time = time;
    }

    public Video() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSample() {
        return sample;
    }

    public void setSample(boolean sample) {
        this.sample = sample;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
