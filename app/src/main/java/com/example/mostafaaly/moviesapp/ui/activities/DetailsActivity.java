package com.example.mostafaaly.moviesapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mostafaaly.moviesapp.OnMovieClickedListener;
import com.example.mostafaaly.moviesapp.R;
import com.example.mostafaaly.moviesapp.models.Movie;
import com.example.mostafaaly.moviesapp.ui.fragments.DetailsFragment;


public class DetailsActivity extends AppCompatActivity implements OnMovieClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        Bundle sentBundle = intent.getExtras();
        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.setArguments(sentBundle);
        detailsFragment.setOnMovieClickListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.DetailsActivity_FrameLayout_DetailsContainer, detailsFragment)
                    .commit();
    }

    @Override
    public void onMovieClicked(Movie movie) {
        Intent intent = new Intent(this, DetailsActivity.class).putExtra(DetailsFragment.ARG_MOVIE_KEY,movie);
        startActivity(intent);
    }
}