package com.adityarana.sangharsh.learning.sangharsh.Model;

public class Banner {
    public Banner() {
    }

    private String id;
    private String imageUrl;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    private String redirectUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Banner(String id, String imageUrl, String redirectUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.redirectUrl = redirectUrl;
    }
}
