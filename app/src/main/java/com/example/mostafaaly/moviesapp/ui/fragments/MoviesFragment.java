package com.example.mostafaaly.moviesapp.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mostafaaly.moviesapp.OnMovieClickedListener;
import com.example.mostafaaly.moviesapp.R;
import com.example.mostafaaly.moviesapp.data.MovieContract;
import com.example.mostafaaly.moviesapp.data.MovieDbHelper;
import com.example.mostafaaly.moviesapp.models.Movie;
import com.example.mostafaaly.moviesapp.models.MoviesResponse;
import com.example.mostafaaly.moviesapp.ui.adapters.MoviesAdapter;
import com.example.mostafaaly.moviesapp.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Mostafa Aly on 21/10/2016.
 */
public class MoviesFragment extends Fragment implements MoviesAdapter.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.MoviesFragment_RecyclerView_MoviesList)
    RecyclerView mMoviesRecyclerView;
    @BindView(R.id.MoviesFragment_SwipeRefreshLayout_RefreshList)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.MoviesFragment_ProgressBar_MoviesLoading)
    ProgressBar mLoadingProgressBar;
    private MoviesAdapter mMoviesAdapter;
    public static final String ARG_SORT_TYPE_NUMBER = "sort_type_number";
    private OnMovieClickedListener mOnMovieClickedListener;
    private SQLiteDatabase mSqLiteDatabase;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this,rootView);
        mSqLiteDatabase = new MovieDbHelper(getContext()).getReadableDatabase();
        mMoviesAdapter = new MoviesAdapter(new ArrayList<Movie>(),getContext(),this,R.layout.movies_list_item);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mContext = getContext();
        mSwipeRefreshLayout.setEnabled(false);
        updateMoviesList();
        return rootView;
    }


    @Override
    public void onClick(Movie movie) {
        mOnMovieClickedListener.onMovieClicked(movie);
    }

    public void setOnMovieClickListener(OnMovieClickedListener onMovieClickedListener) {
        mOnMovieClickedListener = onMovieClickedListener;
    }

    @Override
    public void onRefresh() {
        updateMoviesList();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void updateMoviesList() {

        int sortTypeNumber = getArguments().getInt(ARG_SORT_TYPE_NUMBER);
        String sortType = getResources().getStringArray(R.array.pref_sort_values)[sortTypeNumber];
        if (sortType.toLowerCase().equals(getString(R.string.pref_sort_favorite))) {
            //FetchDatabase();
        } else {
            Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
            Call<MoviesResponse> call = apiService.getMoviesList(sortType,Utils.API_KEY);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getMovies();
                    mMoviesAdapter.clear();
                    mMoviesAdapter.add(movies);
                    mLoadingProgressBar.setVisibility(View.GONE);
                    mMoviesRecyclerView.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setEnabled(true);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Toast.makeText(mContext,"Failed to connect",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void FetchDatabase() {

        String title,id,cover,overview,poster,rate,date;

        String[] projection = {
                MovieContract.MovieEntry.COLUMN_ID,
                MovieContract.MovieEntry.MOVIE_ID,
                MovieContract.MovieEntry.MOVIE_TITLE,
                MovieContract.MovieEntry.MOVIE_OVERVIEW,
                MovieContract.MovieEntry.MOVIE_RATE,
                MovieContract.MovieEntry.MOVIE_DATE,
                MovieContract.MovieEntry.MOVIE_POSTER,
                MovieContract.MovieEntry.MOVIE_COVER
        };

        Cursor cursor = mSqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                projection, null, null, null, null, null
        );
        int i = 0; // play as index of movies list
        Movie[] movies = new Movie[cursor.getCount()];

        if(cursor.getCount() == 0) { // favorite list is empty
            Toast.makeText(getContext(), "There is no Favorite Movies to show", Toast.LENGTH_LONG).show();
        }

        while (cursor.moveToNext() && cursor.getCount() != 0) {
            title = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_TITLE));
            id = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_ID));
            overview = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_OVERVIEW));
            date = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_DATE));
            rate = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_RATE));
            poster = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_POSTER));
            cover = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_COVER));
            //movies[i] = new Movie(title, poster, overview, date, rate, id, cover);
            i++;
        }
            cursor.close();

            if (movies != null) {
                mMoviesAdapter.clear();
                for (Movie movie : movies) {
                    mMoviesAdapter.add(movie);
                }
            }

        }
}