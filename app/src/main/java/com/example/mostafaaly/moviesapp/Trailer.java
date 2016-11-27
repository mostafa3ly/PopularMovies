package com.example.mostafaaly.moviesapp;

import android.widget.TextView;

/**
 * Created by mosta on 11/25/2016.
 */
public class Trailer {

    private String mKey;
    private String mName;

    public Trailer (String key ,String name){

        mName = name;
        mKey = key;
    }

    public String getmKey(){return mKey;}
    public String getmName(){return mName;}

}
