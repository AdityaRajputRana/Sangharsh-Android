package com.adityarana.sangharsh.learning.sangharsh.Model.Notifications;

import java.util.HashMap;

public class Notification {
    String title;
    String icon;
    String image;
    String message;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Object> getTime() {
        return time;
    }

    public void setTime(HashMap<String, Object> time) {
        this.time = time;
    }

    public HashMap<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(HashMap<String, Object> extras) {
        this.extras = extras;
    }

    public Notification() {
    }

    public Notification(String title, String icon, String image, String message, String expiresIn, String id, HashMap<String, Object> time, HashMap<String, Object> extras) {
        this.title = title;
        this.icon = icon;
        this.image = image;
        this.message = message;
        this.expiresIn = expiresIn;
        this.id = id;
        this.time = time;
        this.extras = extras;
    }

    String expiresIn;

    String id;
    HashMap<String, Object> time;
    HashMap<String, Object> extras;
}
