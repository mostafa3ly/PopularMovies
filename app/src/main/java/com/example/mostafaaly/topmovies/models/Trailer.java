package com.example.mostafaaly.topmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mosta on 11/25/2016.
 */
public class Trailer {


    @SerializedName("key")
    @Expose
    private String key;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
