package com.example.mostafaaly.moviesapp.models;

/**
 * Created by mosta on 11/25/2016.
 */
public class Review {

    private String mContent;
    private String mAuthor;

    public Review (String content ,String author){

        mContent = content;
        mAuthor = author;
    }

    public String getmContent(){return mContent;}
    public String getmAuthor(){return mAuthor;}
}
