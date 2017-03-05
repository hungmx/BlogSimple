package com.example.mxhung.blogsimple.model;

/**
 * Created by MXHung on 3/5/2017.
 */

public class Blog {
    public String title;
    public String description;
    public String image;

    public Blog() {
    }

    public Blog(String title, String description, String image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
