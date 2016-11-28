package com.example.mostafaaly.moviesapp;

import android.provider.BaseColumns;

/**
 * Created by mosta on 11/27/2016.
 */
public class MovieContract {

    private MovieContract(){}
    public static class MovieEntry implements BaseColumns{

        //columns name added to db table
        public static final String COLUMN_ID = "_id";
        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_ID = "id";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_RATE = "rate";
        public static final String MOVIE_POSTER = "poster";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_DATE = "date";
    }
}
