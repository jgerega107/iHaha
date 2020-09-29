package com.aftertastephd.ihaha.DataSources;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private String email;
    private String bio;
    private String pfpUriAsString;
    private String uid;
    private String fcmToken;
    private List<String> groups;

    public User(String username, String email, String bio, String uid, String pfpUriAsString, String fcmToken) {
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.pfpUriAsString = pfpUriAsString;
        this.uid = uid;
        this.fcmToken = fcmToken;
        groups = new ArrayList<>();
    }

    public User(String username, String email, String bio, String uid, String fcmToken) {
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.pfpUriAsString = "";
        this.uid = uid;
        this.fcmToken = fcmToken;
        groups = new ArrayList<>();
    }
    public User() {
        //blank constructor for firestore
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPfpUriAsString() {
        return pfpUriAsString;
    }

    public String getUid() {
        return uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void addGroup(String groupInviteCode){
        groups.add(groupInviteCode);
    }

    public void removeGroup(String groupInviteCode){
        groups.remove(groupInviteCode);
    }

}
