package com.aftertastephd.ihaha.DataSources;

public class Photo {
    private String authorUid;
    private String photoUrl;
    private long timestamp;


    public Photo(String authorUid, String photoUrl){
        this.authorUid = authorUid;
        this.photoUrl = photoUrl;
        timestamp = System.currentTimeMillis();
    }
    public Photo(){
        authorUid = "";
        photoUrl = "";
        timestamp = System.currentTimeMillis();
    }
    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
