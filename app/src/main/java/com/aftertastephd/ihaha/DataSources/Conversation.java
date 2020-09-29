package com.aftertastephd.ihaha.DataSources;

public class Conversation {
    private String userUid;
    private String lastMessage;
    private long lastMessageTimestamp;
    private boolean unread;
    private boolean unseenPhoto;

    public Conversation() {
        userUid = "";
        lastMessage = "";
        lastMessageTimestamp = System.currentTimeMillis();
        unread = true;
        unseenPhoto = false;
    }

    public Conversation(String userUid) {
        this.userUid = userUid;
        lastMessage = "";
        lastMessageTimestamp = System.currentTimeMillis();
        unread = true;
        unseenPhoto = false;
    }


    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
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

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isUnseenPhoto() {
        return unseenPhoto;
    }

    public void setUnseenPhoto(boolean unseenPhoto) {
        this.unseenPhoto = unseenPhoto;
    }

}
