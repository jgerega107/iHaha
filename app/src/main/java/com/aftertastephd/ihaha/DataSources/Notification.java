package com.aftertastephd.ihaha.DataSources;

public class Notification {
    private String author;
    private String message;
    private String sendToUid;

    public Notification(String author, String message, String sendToUid) {
        this.author = author;
        this.message = message;
        this.sendToUid = sendToUid;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public String getSendToUid() {
        return sendToUid;
    }
}
