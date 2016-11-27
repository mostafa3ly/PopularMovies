package com.example.mostafaaly.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        inflater.inflate(R.menu.main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            updateMovieList();
        }
        else if(id == R.id.action_setting)
        {
            getActivity().startActivity(new Intent(getActivity(),SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMoviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_movies);
        gridView.setAdapter(mMoviesAdapter);
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


    private void updateMovieList()
    {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity(),mMoviesAdapter);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortType = sharedPreferences.getString(getContext().getString(R.string.pref_sort_key),getContext().getString(R.string.pref_sort_rated));
        movieTask.execute(sortType);

    }


}
