package com.aftertastephd.ihaha.DataSources;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String title;
    private String description;
    private String inviteCode;
    private List<String> uids;
    private String lastMessage;
    private long lastMessageTimestamp;
    private List<Boolean> unreads;
    private String groupPfpUrlAsString;
    private boolean moonbaseAlphaMode;

    public Group(){
        title = "";
        description = "";
        inviteCode = System.currentTimeMillis() + "";
        uids = new ArrayList<>();
        lastMessage = "";
        lastMessageTimestamp = System.currentTimeMillis();
        unreads = new ArrayList<>();
        groupPfpUrlAsString = "";
        moonbaseAlphaMode = false;
    }

    public Group(String title, String description, String uid, boolean moonbaseAlphaMode){
        this.title = title;
        this.description = description;
        inviteCode = System.currentTimeMillis() + "";
        this.uids = new ArrayList<>();
        uids.add(uid);
        this.lastMessage = "";
        lastMessageTimestamp = System.currentTimeMillis();
        this.unreads = new ArrayList<>();
        unreads.add(true);
        this.groupPfpUrlAsString = "";
        this.moonbaseAlphaMode = moonbaseAlphaMode;
    }

    public List<String> getUids() {
        return uids;
    }

    public void setUids(List<String> uids) {
        this.uids = uids;
    }
    public void addUser(String uid){
        uids.add(uid);
        unreads.add(true);
    }
    public void removeUser(String uid){
        int index = uids.indexOf(uid);
        uids.remove(uid);
        unreads.remove(index);
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public List<Boolean> getUnreads() {
        return unreads;
    }

    public void setUnreads(List<Boolean> unreads) {
        this.unreads = unreads;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }


    public String getGroupPfpUrlAsString() {
        return groupPfpUrlAsString;
    }

    public void setGroupPfpUrlAsString(String groupPfpUrlAsString) {
        this.groupPfpUrlAsString = groupPfpUrlAsString;
    }

    public boolean isMoonbaseAlphaMode() {
        return moonbaseAlphaMode;
    }
}
