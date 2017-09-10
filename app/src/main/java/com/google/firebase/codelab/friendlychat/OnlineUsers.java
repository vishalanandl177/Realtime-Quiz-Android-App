package com.google.firebase.codelab.friendlychat;

/**
 * Created by Vishal Anand on 21-11-2016.
 */

class OnlineUsers {
    String name, email, photoUrl;
    boolean online;

    public OnlineUsers() {
    }

    public OnlineUsers(String name, String email, String photoUrl, boolean online) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}