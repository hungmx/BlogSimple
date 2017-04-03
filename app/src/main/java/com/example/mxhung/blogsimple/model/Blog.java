package com.example.mxhung.blogsimple.model;

/**
 * Created by MXHung on 3/5/2017.
 */

public class Blog {
    public String title;
    public String description;
    public String image;
    public String username;
    public String key;
    public String uid;
    public String time;
    public Long like;

    public Blog() {
        // Default constructor required for calls to DataSnapshot.getValue(Blog.class)
    }

    public Blog(String key, String title, String description, String image, String username, String uid, String time, Long like) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.image = image;
        this.username = username;
        this.uid = uid;
        this.time = time;
        this.like = like;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getLike() {
        return like;
    }

    public void setLike(Long like) {
        this.like = like;
    }
}
