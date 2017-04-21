package com.example.mxhung.blogsimple.message;

/**
 * Created by MXHung on 4/10/2017.
 */

public class MessageModel {
    public String time;
    public String text;
    public String sender;

    public MessageModel() {
    }

    public MessageModel(String time, String text, String sender) {
        this.time = time;
        this.text = text;
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
