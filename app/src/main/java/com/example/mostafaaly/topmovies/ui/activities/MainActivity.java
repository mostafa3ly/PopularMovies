package com.example.mostafaaly.topmovies.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mostafaaly.topmovies.R;
import com.example.mostafaaly.topmovies.models.Movie;
import com.example.mostafaaly.topmovies.ui.fragments.DetailsFragment;
import com.example.mostafaaly.topmovies.ui.fragments.MoviesFragment;
import com.example.mostafaaly.topmovies.utilities.OnMovieClickedListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements OnMovieClickedListener,SharedPreferences.OnSharedPreferenceChangeListener {



    @BindView(R.id.MainActivity_DrawerLayout_MainDrawer) DrawerLayout mDrawerLayout; 
    @BindView(R.id.MainActivity_ListView_SortTypesList) ListView mDrawerListView;

    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mSortTypesTitles;
    private int mSortNumber;
    private boolean mTwoPane ;
    private SharedPreferences mSharedPreferences;
    DetailsFragment detailsFragment;
    private int mShownMovieId = 0;
    private static final String ARG_SHOWN_MOVIE_ID = "movie_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTwoPane = findViewById(R.id.MainActivity_FrameLayout_DetailsContainer) != null;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSortNumber = mSharedPreferences.getInt(getString(R.string.pref_sort_key),0);


        mSortTypesTitles = getResources().getStringArray(R.array.pref_sort_options);
        mDrawerListView.setAdapter(new ArrayAdapter<>(this,R.layout.sort_type_list_item, mSortTypesTitles));
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != mSortNumber) {
                    selectSortType(position);
                }
                else {
                    mDrawerLayout.closeDrawer(mDrawerListView);
                }

            }
        });

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setTitle(mSortTypesTitles[mSortNumber]);

        if(savedInstanceState!=null) {
            mShownMovieId = savedInstanceState.getInt(ARG_SHOWN_MOVIE_ID);
        }
        else
        {
            selectSortType(mSortNumber);
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SHOWN_MOVIE_ID,mShownMovieId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClicked(Movie movie) {
        if(mTwoPane)
        {
            if(movie.getId()!=mShownMovieId) {
                mShownMovieId=movie.getId();
                detailsFragment = new DetailsFragment();
                Bundle args = new Bundle();
                args.putParcelable(DetailsFragment.ARG_MOVIE_KEY, movie);
                detailsFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.MainActivity_FrameLayout_DetailsContainer, detailsFragment).commit();
            }
        }
        else
        {
            Intent intent = new Intent(this, DetailsActivity.class).putExtra(DetailsFragment.ARG_MOVIE_KEY,movie);
            startActivity(intent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mSortNumber = mSharedPreferences.getInt(getString(R.string.pref_sort_key),0);
        setTitle(mSortTypesTitles[sharedPreferences.getInt(getString(R.string.pref_sort_key),0)]);
    }

    public void selectSortType(int position)
    {
        MoviesFragment moviesFragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putInt(MoviesFragment.ARG_SORT_TYPE_NUMBER, position);
        moviesFragment.setArguments(args);
        moviesFragment.setOnMovieClickListener(this);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                .replace(R.id.MainActivity_FrameLayout_MoviesContainer,moviesFragment).commit();

        mDrawerListView.setItemChecked(position,true);
        mDrawerLayout.closeDrawer(mDrawerListView);
        mSharedPreferences.edit().putInt(getString(R.string.pref_sort_key),position).apply();
    }
}
