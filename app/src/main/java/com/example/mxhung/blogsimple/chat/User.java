package com.example.mxhung.blogsimple.chat;

/**
 * Created by MXHung on 4/3/2017.
 */

public class User {
    public String name;
    public String image;
    public String uId;

    public User() {
    }

    public User(String name, String image, String uId) {
        this.name = name;
        this.image = image;
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
