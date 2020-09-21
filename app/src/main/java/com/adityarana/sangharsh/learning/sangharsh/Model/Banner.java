package com.adityarana.sangharsh.learning.sangharsh.Model;

public class Banner {
    public Banner() {
    }

    private String id;
    private String imageUrl;
    private String category, subcategory;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

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

    public Banner(String id, String imageUrl, String subcategory, String redirectUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.subcategory = subcategory;
        this.redirectUrl = redirectUrl;
    }
}
