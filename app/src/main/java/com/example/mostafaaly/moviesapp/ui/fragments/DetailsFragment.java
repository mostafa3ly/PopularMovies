package com.example.mostafaaly.moviesapp.ui.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mostafaaly.moviesapp.OnMovieClickedListener;
import com.example.mostafaaly.moviesapp.R;
import com.example.mostafaaly.moviesapp.models.Movie;
import com.example.mostafaaly.moviesapp.models.MoviesResponse;
import com.example.mostafaaly.moviesapp.models.Trailer;
import com.example.mostafaaly.moviesapp.models.TrailersResponse;
import com.example.mostafaaly.moviesapp.ui.activities.MainActivity;
import com.example.mostafaaly.moviesapp.ui.adapters.MoviesAdapter;
import com.example.mostafaaly.moviesapp.ui.adapters.TrailersAdapter;
import com.example.mostafaaly.moviesapp.utilities.Utils;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mosta on 11/9/2016.
 */
public class DetailsFragment extends Fragment implements ObservableScrollViewCallbacks,TrailersAdapter.OnItemClickListener,MoviesAdapter.OnItemClickListener{


    @BindView(R.id.DetailsFragment_ScrollView_DetailsScroll)
    ObservableScrollView mDetailsScrollview;
    @BindView(R.id.DetailsFragment_ImageView_MovieCover)
    ImageView mMovieCoverImageView;
    @BindView(R.id.DetailsFragment_Progressbar_CoverProgress)
    ProgressBar mMovieCoverProgressBar;
    @BindView(R.id.DetailsFragment_Progressbar_PosterProgress)
    ProgressBar mMoviePosterProgressBar;
    @BindView(R.id.DetailsFragment_ImageView_MoviePoster)
    ImageView mMoviePosterImageView;
    @BindView(R.id.DetailsFragment_TextView_MovieTitle)
    TextView mMovieTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_MovieYear)
    TextView mMovieYearTextView;
    @BindView(R.id.DetailsFragment_RatingBar_MovieRating)
    RatingBar mMovieVoteRatingBar;
    @BindView(R.id.DetailsFragment_TextView_MovieGenres)
    TextView getmMovieGenresTextView;
    @BindView(R.id.DetailsFragment_RecyclerView_TrailersList)
    RecyclerView mTrailersRecyclerView;
    @BindView(R.id.DetailsFragment_TextView_TrailersTitle)
    TextView mTrailersTitleTextView;
    @BindView(R.id.DetailsFragment_RecyclerView_SimilarMoviesList)
    RecyclerView mSimilarMoviesRecyclerView;
    @BindView(R.id.DetailsFragment_TextView_SimilarMoviesTitle)
    TextView mSimilarMoviesTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_CastNames)
    TextView mMovieCastNamesTextView;
    @BindView(R.id.DetailsFragment_TextView_CastTitle)
    TextView mMovieCastTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_CrewNames)
    TextView mMovieCrewNamesTextView;
    @BindView(R.id.DetailsFragment_TextView_CrewTitle)
    TextView mMovieCrewTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_MovieOverview)
    TextView mMovieOverviewTextView;
    @BindView(R.id.DetailsActivity_ImageView_Expand)
    ImageView mMovieOverviewExpandImageView;
    public final static String ARG_MOVIE_KEY = "movie_key";
    private TrailersAdapter mTrailersAdapter;
    private MoviesAdapter mSimilarMoviesAdapter;
    private OnMovieClickedListener mOnSimilarMovieClickListener;
    private Movie mShownMovie;
    private boolean mIsFavoriteMovie;
    private boolean mIsMovieOverviewExpanded;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof MainActivity)
            setOnMovieClickListener(((MainActivity)getActivity()));
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details,container,false);
        ButterKnife.bind(this,rootView);
        mDetailsScrollview.setScrollViewCallbacks(this);


        Bundle sentBundle = getArguments();
        mShownMovie = (Movie)sentBundle.getParcelable(ARG_MOVIE_KEY);
        if(Utils.isFavoriteMovie(mShownMovie.getId().toString(),getContext()))
        {
            mIsFavoriteMovie = true;
            //check the fav star
        }
        mTrailersAdapter = new TrailersAdapter(new ArrayList<Trailer>(),getContext(),this);
        mSimilarMoviesAdapter = new MoviesAdapter(new ArrayList<Movie>(),getContext(),this,R.layout.similar_movies_list_item);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        mSimilarMoviesRecyclerView.setAdapter(mSimilarMoviesAdapter);



        fetchMovieDetails();
        updateTrailersList();
        updateUI();
        fetchSimilarMovies();


        Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
        Call<JsonObject> call = apiService.getMovieCredits(mShownMovie.getId(),Utils.API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject creditsResponse = response.body();
                JsonArray castList = creditsResponse.getAsJsonArray("cast");
                JsonArray crewList = creditsResponse.getAsJsonArray("crew");


                mMovieCastNamesTextView.setText(Utils.getMovieCastString(castList));
                mMovieCastTitleTextView.setVisibility(View.VISIBLE);
                mMovieCrewNamesTextView.setText(Utils.getMovieCrewString(crewList));
                mMovieCrewTitleTextView.setVisibility(View.VISIBLE);

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });



        /*imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onMovieClicked(View view) {
                MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
                SQLiteDatabase sqLiteDatabase = movieDbHelper.getWritableDatabase();
                mShownMovie.changeFavoriteCase();
                if(mShownMovie.isFavorite()) {
                    imageButton.setImageResource(R.drawable.ic_star_yellow_48dp);
                    try {
                        //insert the mShownMovie into db
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.MovieEntry.MOVIE_ID, mShownMovie.getId());
                        values.put(MovieContract.MovieEntry.MOVIE_TITLE, mShownMovie.getTitle());
                        values.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, mShownMovie.getOverview());
                        values.put(MovieContract.MovieEntry.MOVIE_DATE, mShownMovie.getReleaseDate());
                        values.put(MovieContract.MovieEntry.MOVIE_POSTER, mShownMovie.getPosterPath());
                        values.put(MovieContract.MovieEntry.MOVIE_RATE, mShownMovie.getRating());
                        values.put(MovieContract.MovieEntry.MOVIE_COVER, mShownMovie.getCoverPath());
                        long newId  = sqLiteDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                        Toast.makeText(getContext(),mShownMovie.getTitle()+" was added to favorite list",Toast.LENGTH_SHORT).show();
                    }
                    catch (SQLException exception)
                    {
                        Log.i("Error","SQLite Error");
                    }
                }
                else
                {
                    //removing mShownMovie from db
                    imageButton.setImageResource(R.drawable.ic_star_border_black_48dp);
                    sqLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME,SELECTION,null);
                    Toast.makeText(getContext(),mShownMovie.getTitle()+" was removed from favorite list",Toast.LENGTH_SHORT).show();
                }
            }
        });*/





        return rootView;
    }

    private void fetchSimilarMovies()
    {
        Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
        Call<MoviesResponse> call = apiService.getSimilarMovies(mShownMovie.getId(),Utils.API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                List<Movie> similarMovies = response.body().getMovies();
                mSimilarMoviesAdapter.clear();
                mSimilarMoviesAdapter.add(similarMovies);
                if (similarMovies.size()>0)
                    mSimilarMoviesTitleTextView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
            }
        });
    }

    private void fetchMovieDetails() {

        Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
        Call<Movie> call = apiService.getMovieDetails(mShownMovie.getId(),Utils.API_KEY);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                mShownMovie.setGenres(movie.getGenres());
                mShownMovie.setBudget(movie.getBudget());
                mShownMovie.setRevenue(movie.getRevenue());
                mShownMovie.setStatus(movie.getStatus());
                updateRestUI();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
            }
        });

    }

    public void setOnMovieClickListener(OnMovieClickedListener onMovieClickedListener) {
        mOnSimilarMovieClickListener = onMovieClickedListener;
    }

    private void updateRestUI() {
        getmMovieGenresTextView.setText(Utils.getMovieGenresString(mShownMovie.getGenres()));
    }


    private void updateUI(){
        Picasso.with(getContext()).load(Utils.buildImageUrl(780,mShownMovie.getBackdropPath()))
                .placeholder(R.drawable.movie_cover_placeholder)
                .error(R.drawable.movie_cover_placeholder)
                .fit().into(mMovieCoverImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mMovieCoverProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });
        Picasso.with(getContext()).load(Utils.buildImageUrl(500,mShownMovie.getPosterPath()))
                .placeholder(R.drawable.movie_details_poster_placeholder)
                .error(R.drawable.movie_details_poster_placeholder)
                .fit().into(mMoviePosterImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mMoviePosterProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
            }
        });
        mMovieTitleTextView.setText(mShownMovie.getTitle());
        mMovieYearTextView.setText(mShownMovie.getReleaseDate().substring(0,4));
        mMovieVoteRatingBar.setRating(mShownMovie.getVoteAverage().floatValue()/2f);
        mMovieOverviewTextView.setText(mShownMovie.getOverview());

        mMovieOverviewExpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMovieOverviewExpanded)
                {
                    mMovieOverviewTextView.setMaxLines(2);
                    mMovieOverviewExpandImageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
                }
            else
                {
                    mMovieOverviewTextView.setMaxLines(Integer.MAX_VALUE);
                    mMovieOverviewExpandImageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
                }
                mIsMovieOverviewExpanded=!mIsMovieOverviewExpanded;
            }
        });

    }


    private void updateTrailersList(){

        Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
        Call<TrailersResponse> call = apiService.getMovieTrailers(mShownMovie.getId(),Utils.API_KEY);
        call.enqueue(new Callback<TrailersResponse>() {
            @Override
            public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                List<Trailer> trailers = response.body().getTrailers();
                mTrailersAdapter.clear();
                mTrailersAdapter.add(trailers);
                if (trailers.size()>0)
                    mTrailersTitleTextView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {
            }
        });

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewCompat.setTranslationY(mMovieCoverImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onClick(Trailer trailer) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Utils.YOUTUBE_WATCH_TRAILERS_BASE_URI + trailer.getKey()));
        startActivity(intent);
    }

    @Override
    public void onClick(Movie movie) {
            mOnSimilarMovieClickListener.onMovieClicked(movie);
    }
}
