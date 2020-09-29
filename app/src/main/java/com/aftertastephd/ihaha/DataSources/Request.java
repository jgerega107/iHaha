package com.aftertastephd.ihaha.DataSources;

public class Request {
    private String author;
    public boolean isReturned;
    public Request(){
        author = "";
        isReturned = false;
    }
    public Request(String author){
        this.author = author;
        isReturned = false;
    }

    public String getAuthor(){
        return author;
    }

    public boolean isReturned(){
        return isReturned;
    }
}
