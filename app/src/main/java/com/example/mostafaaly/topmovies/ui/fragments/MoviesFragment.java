package com.example.mostafaaly.topmovies.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mostafaaly.topmovies.R;
import com.example.mostafaaly.topmovies.data.MovieContract;
import com.example.mostafaaly.topmovies.models.Movie;
import com.example.mostafaaly.topmovies.models.MoviesResponse;
import com.example.mostafaaly.topmovies.ui.adapters.MoviesAdapter;
import com.example.mostafaaly.topmovies.utilities.OnMovieClickedListener;
import com.example.mostafaaly.topmovies.utilities.Utils;

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
public class MoviesFragment extends Fragment implements MoviesAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.MoviesFragment_RecyclerView_MoviesList)
    RecyclerView mMoviesRecyclerView;
    @BindView(R.id.MoviesFragment_SwipeRefreshLayout_RefreshList)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.MoviesFragment_ProgressBar_MoviesLoading)
    ProgressBar mLoadingProgressBar;
    @BindView(R.id.MoviesFragment_TextView_EmptyMessage)
    TextView mEmptyListTextView;
    private MoviesAdapter mMoviesAdapter;
    public static final String ARG_SORT_TYPE_NUMBER = "sort_type_number";
    private OnMovieClickedListener mOnMovieClickedListener;
    private Context mContext;


    private static final String POSITION = "position";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, rootView);

        mContext = getContext();
        mMoviesAdapter = new MoviesAdapter(new ArrayList<Movie>(), getContext(), this, R.layout.movies_list_item);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);
        String tag  = (String) mMoviesRecyclerView.getTag();
        mMoviesRecyclerView.setLayoutManager(new GridLayoutManager(mContext,Integer.valueOf(tag)));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnabled(false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null)
        {
            mMoviesRecyclerView.getLayoutManager().scrollToPosition(savedInstanceState.getInt(POSITION));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION,((GridLayoutManager)mMoviesRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition());
    }



    @Override
    public void onResume() {
        super.onResume();
        updateMoviesList();
        setOnMovieClickListener((OnMovieClickedListener)(getActivity()));
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
        mSwipeRefreshLayout.setRefreshing(false);
        updateMoviesList();

    }



    private void updateMoviesList() {

        int sortTypeNumber = getArguments().getInt(ARG_SORT_TYPE_NUMBER);
        String sortType = getResources().getStringArray(R.array.pref_sort_values)[sortTypeNumber];
        if (sortType.toLowerCase().equals(getString(R.string.pref_sort_favorite))) {
            fetchFavoriteMovies();
        } else {
            if (Utils.checkNetworkConnection(mContext)) {
                mLoadingProgressBar.setVisibility(View.VISIBLE);
                mMoviesRecyclerView.setVisibility(View.GONE);
                mEmptyListTextView.setVisibility(View.GONE);
                Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
                Call<MoviesResponse> call = apiService.getMoviesList(sortType, Utils.API_KEY);
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        MoviesResponse moviesResponse = response.body();
                        if (moviesResponse != null) {
                            List<Movie> movies = moviesResponse.getMovies();
                            mMoviesAdapter.clear();
                            mMoviesAdapter.add(movies);
                            mEmptyListTextView.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setEnabled(true);
                            showOnlineStatus();
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        showOfflineStatus();
                    }
                });
            } else {
                showOfflineStatus();
            }
        }
    }

    private void showOnlineStatus() {
        mLoadingProgressBar.setVisibility(View.GONE);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showOfflineStatus() {
        mLoadingProgressBar.setVisibility(View.GONE);
        mEmptyListTextView.setText(mContext.getString(R.string.no_connection));
        if (mMoviesRecyclerView.getVisibility() == View.GONE)
            mEmptyListTextView.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setEnabled(true);
    }

    private void fetchFavoriteMovies() {

        String[] projection = {
                MovieContract.MovieEntry.COLUMN_ID,
                MovieContract.MovieEntry.MOVIE_ID,
                MovieContract.MovieEntry.MOVIE_TITLE,
                MovieContract.MovieEntry.MOVIE_OVERVIEW,
                MovieContract.MovieEntry.MOVIE_RATE,
                MovieContract.MovieEntry.MOVIE_DATE,
                MovieContract.MovieEntry.MOVIE_POSTER,
                MovieContract.MovieEntry.MOVIE_COVER,
                MovieContract.MovieEntry.MOVIE_POSTER_IMAGE
        };

        Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                projection, null, null, null
        );

        if(cursor!=null) {
            if (cursor.getCount() == 0) {
                mEmptyListTextView.setText(mContext.getString(R.string.no_favorites));
                mEmptyListTextView.setVisibility(View.VISIBLE);
            }


            List<Movie> movies = new ArrayList<>();

            while (cursor.moveToNext()) {
                Movie movie = new Movie();
                movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_TITLE)));
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_ID))));
                movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_OVERVIEW)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_DATE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_RATE))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_POSTER)));
                movie.setBackdropPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_COVER)));
                movie.setPosterImage(cursor.getBlob(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_POSTER_IMAGE)));
                movies.add(movie);
            }
            mMoviesAdapter.clear();
            mMoviesAdapter.add(movies);
            showOnlineStatus();
            cursor.close();
        }
    }



}