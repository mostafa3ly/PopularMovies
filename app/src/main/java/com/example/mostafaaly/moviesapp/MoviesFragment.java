package com.example.mostafaaly.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Mostafa Aly on 21/10/2016.
 */
public class MoviesFragment extends Fragment {

    private MoviesAdapter mMoviesAdapter;
    private MovieListener mMovieListener;



    public void setMovieListener(MovieListener movieListener) {
        mMovieListener = movieListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovieList();
        } else if (id == R.id.action_setting) {
            getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //this block of code to create custom array adapter object to list the movie posters in the main fragment
        mMoviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_movies);
        gridView.setAdapter(mMoviesAdapter);

        //this block of code handle clicking on movie to show its details
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = mMoviesAdapter.getItem(i);
                mMovieListener.setMovieListener(movie);
            }
        });
        return rootView;
    }


    public void onStart() {
        super.onStart();
        updateMovieList();
    }


    private void updateMovieList() {

        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMoviesAdapter);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortType = sharedPreferences.getString(getContext().getString(R.string.pref_sort_key), getContext().getString(R.string.pref_sort_rated));

        //if favorite is chosen so fetch db and not network
        if (sortType.toLowerCase().equals("favorite")) {
            FetchDatabase();
        } else {
            movieTask.execute(sortType);
        }

    }

    /**
     * void method for fetching the database and returning the movie fetched to the adapter to list it
     */
    public void FetchDatabase() {

        MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase sqLiteDatabase = movieDbHelper.getReadableDatabase();

        String title;
        String id;
        String overview;
        String poster;
        String rate;
        String date;

        // projection of columns queried from db
        String[] projection = {
                MovieContract.MovieEntry.COLUMN_ID,
                MovieContract.MovieEntry.MOVIE_ID,
                MovieContract.MovieEntry.MOVIE_TITLE,
                MovieContract.MovieEntry.MOVIE_OVERVIEW,
                MovieContract.MovieEntry.MOVIE_RATE,
                MovieContract.MovieEntry.MOVIE_DATE,
                MovieContract.MovieEntry.MOVIE_POSTER
        };

        Cursor cursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                projection, null, null, null, null, null
        );
        int i = 0; // play as index of movies list
        Movie[] movies = new Movie[cursor.getCount()];

        if(cursor.getCount() == 0) { // favorite list is empty
            Toast.makeText(getContext(), "There is no Favorite Movies to show", Toast.LENGTH_LONG).show();
        }

        //this block of code is create movie object from the data returned from db by cursor
        while (cursor.moveToNext() && cursor.getCount() != 0) {
            title = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_TITLE));
            id = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_ID));
            overview = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_OVERVIEW));
            date = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_DATE));
            rate = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_RATE));
            poster = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_POSTER));
            movies[i] = new Movie(title, poster, overview, date, rate, id);
            i++;
        }
            cursor.close();

            if (movies != null) {
                mMoviesAdapter.clear();
                for (Movie movie : movies) {
                    mMoviesAdapter.add(movie);
                    Log.i("movie", movie.getMoviePoster());

                }
            }

        }
    }

