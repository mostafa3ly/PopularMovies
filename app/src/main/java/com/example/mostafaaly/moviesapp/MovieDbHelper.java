package com.example.mostafaaly.moviesapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mostafaaly.moviesapp.MovieContract;

/**
 * Created by mosta on 11/27/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE "+ MovieContract.MovieEntry.TABLE_NAME + " ( " +
                MovieContract.MovieEntry.MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_RATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_POSTER + " TEXT NOT NULL " + ");";
                Log.i("Creation error ",SQL_CREATE_MOVIE_TABLE);
                sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
