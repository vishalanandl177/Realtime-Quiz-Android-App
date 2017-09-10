package com.google.firebase.codelab.friendlychat;

/**
 * Created by Vishal Anand on 10-10-2016.
 */
public class UserInfo {

    String name, email, photoURl;

    public UserInfo() {
    }

    public UserInfo(String name, String photoURl, String email) {
        this.name = name;
        this.photoURl = photoURl;
        this.email = email;
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

    public String getPhotoURl() {
        return photoURl;
    }

    public void setPhotoURl(String photoURl) {
        this.photoURl = photoURl;
    }
}
