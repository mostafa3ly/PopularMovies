package com.example.mostafaaly.topmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mosta on 11/27/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS "+ MovieContract.MovieEntry.TABLE_NAME + " ( " +
                MovieContract.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_RATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_COVER + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_POSTER_IMAGE + " BLOB NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_POSTER + " TEXT NOT NULL " + ");";

                sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
