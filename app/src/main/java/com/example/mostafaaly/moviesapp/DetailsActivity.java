package com.example.mostafaaly.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class DetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent sentIntent = getIntent();
        Bundle sentBundle = sentIntent.getExtras();
        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.setArguments(sentBundle);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, detailsFragment)
                    .commit();
        }


    }
}
