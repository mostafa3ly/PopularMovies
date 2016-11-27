package com.example.mostafaaly.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements MovieListener{

    private boolean mTwoPane = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MoviesFragment moviesFragment = new MoviesFragment();
        moviesFragment.setMovieListener(this);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_container, moviesFragment)
                    .commit();

    if(findViewById(R.id.movie_details_container) != null)
    {
        mTwoPane = true;
    }
    }




    @Override
    public void setMovieListener(Movie movie) {
        if(mTwoPane)
        {
            DetailsFragment detailsFragment = new DetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Movie",movie);
            detailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_details_container,detailsFragment).commit();
        }
        else
        {
            Intent detailIntent = new Intent(this, DetailsActivity.class).putExtra("Movie",movie);
            startActivity(detailIntent);
        }
    }
}