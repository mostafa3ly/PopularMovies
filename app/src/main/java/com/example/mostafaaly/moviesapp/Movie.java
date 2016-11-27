package com.example.mostafaaly.moviesapp;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Mostafa Aly on 21/10/2016.
 */
public class Movie  implements Serializable{

    public static final String TEMP_DURATION = "120min";
    public static final String MAX_RATING = "10";

    private String mMovieTitle;
    private String mMoviePoster;
    private String mMovieOverview;
    private String mMovieRating;
    private String mMovieDate;
    private String mMovieId;
    private boolean mIsFavorite = false;




    public Movie (String movieTitle, String moviePoster, String movieOverview, String movieDate, String movieRating, String movieId)
    {
       mMovieDate = movieDate;
        mMovieOverview = movieOverview;
        mMoviePoster = moviePoster;
        mMovieTitle = movieTitle;
        mMovieRating = movieRating;
        mMovieId = movieId;

    }
    public boolean getFavoriteCase()
    {
        return mIsFavorite;
    }
    public void changeFavoriteCase(){

        mIsFavorite=!mIsFavorite;
    }
    public String getMovieTitle ()
    {
        return mMovieTitle;
    }
    public String getMoviePoster(){
        return mMoviePoster;
    }
    public String getMovieOverview (){return mMovieOverview;}
    public String getMovieDate (){return mMovieDate;}
    public String getMovieRating() {return mMovieRating;}
    public String getMovieId(){return mMovieId;}
}
