package com.aftertastephd.ihaha.DataSources;

public class Message {
    private String authorUid;
    private String message;
    private long timestamp;
    private boolean picture;

    public Message(String message, String authorUid, boolean picture) {
        this.message = message;
        timestamp = System.currentTimeMillis();
        this.authorUid = authorUid;
        this.picture = picture;
    }

    public Message(){
        message = "";
        timestamp = System.currentTimeMillis();
        authorUid = "";
        picture = false;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public String getAuthorUid(){
        return authorUid;
    }

    public boolean isPicture(){
        return picture;
    }
}
