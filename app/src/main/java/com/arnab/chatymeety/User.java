package com.arnab.chatymeety;

public class User {
    private String name,status,imageLink,thumbnail;
    private boolean online;

    public User() {

    }

    public User(String name, String status, String imageLink, String thumbnail,boolean online) {
        this.name = name;
        this.status = status;
        this.imageLink = imageLink;
        this.thumbnail = thumbnail;
        this.online=online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean name) {
        this.online = online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
