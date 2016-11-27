package com.example.mostafaaly.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOError;
import java.io.IOException;

/**
 * Created by mosta on 11/27/2016.
 */
public class ManageDataBase extends AsyncTask<String,Void,Long> {


    private final Context mContext;
    private final Movie movie;
    public ManageDataBase(Context context,Movie movie){
        mContext = context;
        this.movie = movie;

    }
    @Override
    protected Long doInBackground(String... params) {
        Cursor cursor = null;

        if(params[0].equals("write"))
        {

            try {
                MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
                SQLiteDatabase sqLiteDatabase = movieDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.MOVIE_ID, movie.getMovieId());
                values.put(MovieContract.MovieEntry.MOVIE_TITLE, movie.getMovieTitle());
                values.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, movie.getMovieOverview());
                values.put(MovieContract.MovieEntry.MOVIE_DATE, movie.getMovieDate());
                values.put(MovieContract.MovieEntry.MOVIE_POSTER, movie.getMoviePoster());
                values.put(MovieContract.MovieEntry.MOVIE_RATE, movie.getMovieRating());
                return sqLiteDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
            }
            catch (SQLException exception)
            {
                Log.i("Error","SQLite Error");
                return null;
            }

        }

        try
        {
            MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
            SQLiteDatabase sqLiteDatabase = movieDbHelper.getReadableDatabase();
            String title;
            String id;
            String overview;
            String poster;
            String rate;
            String date;


            String[] projection = {
                    MovieContract.MovieEntry.MOVIE_ID,
                    MovieContract.MovieEntry.MOVIE_TITLE,
                    MovieContract.MovieEntry.MOVIE_OVERVIEW,
                    MovieContract.MovieEntry.MOVIE_RATE,
                    MovieContract.MovieEntry.MOVIE_DATE,
                    MovieContract.MovieEntry.MOVIE_POSTER
            };

             cursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                    projection,null,null,null,null,null
            );
            return Long.valueOf(cursor.getCount());
        }
        catch (SQLException exception)
        {
            Log.i("Error","SQLite Error");
            return null;
        }
        finally {
            if(cursor != null)
                try {
                    cursor.close();
                }
                catch (NullPointerException e)
                {
                    Log.i("Error","Cursor close Error");
                }

        }

    }
}
