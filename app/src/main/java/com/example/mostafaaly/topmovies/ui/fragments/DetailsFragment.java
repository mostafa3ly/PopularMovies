package com.example.mostafaaly.topmovies.ui.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafaaly.topmovies.R;
import com.example.mostafaaly.topmovies.data.MovieContract;
import com.example.mostafaaly.topmovies.models.Movie;
import com.example.mostafaaly.topmovies.models.MoviesResponse;
import com.example.mostafaaly.topmovies.models.Review;
import com.example.mostafaaly.topmovies.models.ReviewsResponse;
import com.example.mostafaaly.topmovies.models.Trailer;
import com.example.mostafaaly.topmovies.models.TrailersResponse;
import com.example.mostafaaly.topmovies.ui.activities.MainActivity;
import com.example.mostafaaly.topmovies.ui.adapters.MoviesAdapter;
import com.example.mostafaaly.topmovies.ui.adapters.ReviewsAdapter;
import com.example.mostafaaly.topmovies.ui.adapters.TrailersAdapter;
import com.example.mostafaaly.topmovies.utilities.OnMovieClickedListener;
import com.example.mostafaaly.topmovies.utilities.Utils;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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


    private static String LOG_TAG = DetailsFragment.class.getSimpleName();

    @BindView(R.id.DetailsFragment_ScrollView_DetailsScroll)
    ObservableScrollView mDetailsScrollview;
    @BindView(R.id.DetailsFragment_ImageView_MovieCover)
    ImageView mMovieCoverImageView;
    @BindView(R.id.DetailsFragment_ImageView_MoviePoster)
    ImageView mMoviePosterImageView;
    @BindView(R.id.DetailsFragment_TextView_MovieTitle)
    TextView mMovieTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_MovieYear)
    TextView mMovieYearTextView;
    @BindView(R.id.DetailsFragment_RatingBar_MovieRating)
    RatingBar mMovieVoteRatingBar;
    @BindView(R.id.DetailsFragment_RecyclerView_TrailersList)
    RecyclerView mTrailersRecyclerView;
    @BindView(R.id.DetailsFragment_TextView_TrailersTitle)
    TextView mTrailersTitleTextView;
    @BindView(R.id.DetailsFragment_RecyclerView_SimilarMoviesList)
    RecyclerView mSimilarMoviesRecyclerView;
    @BindView(R.id.DetailsFragment_TextView_SimilarMoviesTitle)
    TextView mSimilarMoviesTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_Starring)
    TextView mMovieStarringTextView;
    @BindView(R.id.DetailsFragment_TextView_Producers)
    TextView mMovieProducersTextView;
    @BindView(R.id.DetailsFragment_TextView_Directors)
    TextView mMovieDirectorsTextView;
    @BindView(R.id.DetailsFragment_TextView_MovieOverview)
    TextView mMovieOverviewTextView;
    @BindView(R.id.DetailsActivity_ImageView_Expand)
    ImageView mMovieOverviewExpandImageView;
    @BindView(R.id.DetailsFragment_TextView_StatusTitle)
    TextView mMovieStatusTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_Status)
    TextView mMovieStatusTextView;
    @BindView(R.id.DetailsFragment_TextView_BudgetTitle)
    TextView mMovieBudgetTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_Budget)
    TextView mMovieBudgetTextView;
    @BindView(R.id.DetailsFragment_TextView_RevenueTitle)
    TextView mMovieRevenueTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_Revenue)
    TextView mMovieRevenueTextView;
    @BindView(R.id.DetailsFragment_TextView_HomepageTitle)
    TextView mMovieHomepageTitleTextView;
    @BindView(R.id.DetailsFragment_TextView_Homepage)
    TextView mMovieHomepageTextView;
    @BindView(R.id.DetailsFragment_TextView_MoreInformationTitle)
    TextView mMovieMoreInformationTitleTextView;
    @BindView(R.id.DetailsFragment_Progressbar_FetchedDataProgress)
    ProgressBar mMovieFetchedDataProgressBar;
    @BindView(R.id.DetailsFragment_RecyclerView_GenresList)
    RecyclerView mMoviesGenresRecyclerView;
    @BindView(R.id.DetailsActivity_FloatingActionButton_Favorite)
    FloatingActionButton mAddToFavoriteFloatingActionButton;
    @BindView(R.id.DetailsFragment_TextView_ReviewsTitle)
    TextView mMovieReviewTitleTextView;
    @BindView(R.id.DetailsFragment_RecyclerView_ReviewsList)
    RecyclerView mReviewsRecyclerView;
    @BindView(R.id.details_view)
    LinearLayout detailsView;

    public final static String ARG_MOVIE_KEY = "movie_key";
    private TrailersAdapter mTrailersAdapter;
    private MoviesAdapter mSimilarMoviesAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private OnMovieClickedListener mOnSimilarMovieClickListener;
    private Movie mShownMovie;
    private boolean mIsFavoriteMovie;
    private boolean mIsMovieOverviewExpanded;
    private Context mContext;
    private int dataFinishedCount = 0;
    ActionBar mActionBar;
    private static final String SCROLL_VIEW_POSITION = "position";

    private int position = 0;








    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details,container,false);
        ButterKnife.bind(this,rootView);
        mDetailsScrollview.setScrollViewCallbacks(this);
        mContext = getContext();

        Bundle sentBundle = getArguments();
        mShownMovie = (Movie)sentBundle.getParcelable(ARG_MOVIE_KEY);
        mTrailersAdapter = new TrailersAdapter(new ArrayList<Trailer>(),mContext,this);
        mReviewsAdapter = new ReviewsAdapter(new ArrayList<Review>(),mContext);
        mSimilarMoviesAdapter = new MoviesAdapter(new ArrayList<Movie>(),mContext,this,R.layout.similar_movies_list_item);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        mSimilarMoviesRecyclerView.setAdapter(mSimilarMoviesAdapter);

        if(Utils.isFavoriteMovie(mShownMovie.getId().toString(),mContext))
        {
            String[] projection = {
                    MovieContract.MovieEntry.MOVIE_POSTER_IMAGE
            };
            final String SELECTION = MovieContract.MovieEntry.MOVIE_ID + " = " + mShownMovie.getId();
            Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    projection, SELECTION, null, null
            );
            mIsFavoriteMovie = true;
            mAddToFavoriteFloatingActionButton.setImageResource(R.drawable.star_on);
            if(cursor!=null) {
                cursor.moveToFirst();
                mShownMovie.setPosterImage(cursor.getBlob(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.MOVIE_POSTER_IMAGE)));
                cursor.close();
            }
        }
        updateUI();
        if(Utils.checkNetworkConnection(mContext)) {
            fetchMovieDetails();
            updateTrailersList();
            updateReviewsList();
            fetchSimilarMovies();
            fetchMovieCredits();
        }
        else
        {
            dataFinishedCount = 5;
            checkAllFetchFinish();
        }



        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCROLL_VIEW_POSITION,position);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof MainActivity)
            setOnMovieClickListener(((MainActivity)getActivity()));
        else {
            mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            setActionBarStyleAtTop();
        }

        if(savedInstanceState!=null) {
            position = savedInstanceState.getInt(SCROLL_VIEW_POSITION);
        }
    }




    private void fetchMovieCredits() {
        Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
        Call<JsonObject> call = apiService.getMovieCredits(mShownMovie.getId(),Utils.API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject creditsResponse = response.body();

                if(creditsResponse!=null) {
                    JsonArray castList = creditsResponse.getAsJsonArray("cast");
                    JsonArray crewList = creditsResponse.getAsJsonArray("crew");


                    mMovieStarringTextView.setText(Utils.getSpannableMovieCreditString(
                            mContext.getString(R.string.starring, Utils.getMovieStarringString(castList)), 8));
                    mMovieProducersTextView.setText(Utils.getSpannableMovieCreditString(
                            mContext.getString(R.string.produced_by, Utils.getMovieProducersString(crewList, "Producer")), 11));
                    mMovieDirectorsTextView.setText(Utils.getSpannableMovieCreditString(
                            mContext.getString(R.string.directed_by, Utils.getMovieProducersString(crewList, "Director")), 11));
                }
                checkAllFetchFinish();
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                checkAllFetchFinish();
            }
        });

    }

    private void checkAllFetchFinish() {
        dataFinishedCount++;
        if(dataFinishedCount>4) {
            mMovieFetchedDataProgressBar.setVisibility(View.GONE);
            //detailsView.setVisibility(View.VISIBLE);
            mDetailsScrollview.setVisibility(View.VISIBLE);
            mDetailsScrollview.scrollVerticallyTo(position);
        }
    }

    private void fetchSimilarMovies()
    {
        Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
        Call<MoviesResponse> call = apiService.getSimilarMovies(mShownMovie.getId(),Utils.API_KEY);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                MoviesResponse moviesResponse = response.body();
                if(moviesResponse!=null) {
                    List<Movie> similarMovies = moviesResponse.getMovies();
                    mSimilarMoviesAdapter.clear();
                    mSimilarMoviesAdapter.add(similarMovies);
                    if (similarMovies.size() > 0)
                        mSimilarMoviesTitleTextView.setVisibility(View.VISIBLE);
                }
                checkAllFetchFinish();
            }
            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                checkAllFetchFinish();
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
                if(movie!=null) {
                    mShownMovie.setGenres(movie.getGenres());
                    mShownMovie.setBudget(movie.getBudget());
                    mShownMovie.setRevenue(movie.getRevenue());
                    mShownMovie.setStatus(movie.getStatus());
                    mShownMovie.setHomepage(movie.getHomepage());
                    updateRestUI();
                }
                checkAllFetchFinish();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                checkAllFetchFinish();
            }
        });

    }




    private void updateRestUI() {
        boolean hasMoreInfo = false;


        mMoviesGenresRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.genre_list_item, parent, false);
                return new RecyclerView.ViewHolder(itemView) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TextView)holder.itemView.findViewById(R.id.DetailsFragment_TextView_GenreName)).setText(Utils.getMovieGenresNames(mShownMovie.getGenres())[position]);
            }

            @Override
            public int getItemCount() {
                return mShownMovie.getGenres().size();
            }
        });

        mMovieStatusTextView.setText(mShownMovie.getStatus());
        mMovieBudgetTextView.setText(String.valueOf(mShownMovie.getBudget()));
        mMovieRevenueTextView.setText(String.valueOf(mShownMovie.getRevenue()));
        mMovieHomepageTextView.setText(mShownMovie.getHomepage());

        if(mShownMovie.getStatus()!=null && !mShownMovie.getStatus().isEmpty()) {
            mMovieStatusTitleTextView.setVisibility(View.VISIBLE);
            mMovieStatusTextView.setVisibility(View.VISIBLE);
            hasMoreInfo=true;
        }
        if(mShownMovie.getRevenue()>0) {
            mMovieRevenueTitleTextView.setVisibility(View.VISIBLE);
            mMovieRevenueTextView.setVisibility(View.VISIBLE);
            hasMoreInfo=true;
        }
        if(mShownMovie.getBudget()>0) {
            mMovieBudgetTitleTextView.setVisibility(View.VISIBLE);
            mMovieBudgetTextView.setVisibility(View.VISIBLE);
            hasMoreInfo = true;
        }
        if(mShownMovie.getHomepage()!=null && !mShownMovie.getHomepage().isEmpty()) {
            mMovieHomepageTitleTextView.setVisibility(View.VISIBLE);
            mMovieHomepageTextView.setVisibility(View.VISIBLE);
            hasMoreInfo = true;
        }
        if(hasMoreInfo)
            mMovieMoreInformationTitleTextView.setVisibility(View.VISIBLE);

    }


    private void updateUI(){
        Picasso.with(mContext).load(Utils.buildImageUrl(780,mShownMovie.getBackdropPath()))
                .placeholder(R.drawable.movie_cover_placeholder)
                .error(R.drawable.movie_cover_placeholder)
                .fit().into(mMovieCoverImageView);

        if(mIsFavoriteMovie && !Utils.checkNetworkConnection(mContext)) {
            if(mShownMovie.getPosterImage()!=null) {
                mMoviePosterImageView.setImageBitmap(BitmapFactory.decodeByteArray(mShownMovie.getPosterImage(), 0, mShownMovie.getPosterImage().length));
                mAddToFavoriteFloatingActionButton.setVisibility(View.VISIBLE);
                mAddToFavoriteFloatingActionButton.animate().scaleX(1f).scaleY(1f).setDuration(100);
            }
        }
        else {
            Picasso.with(mContext).load(Utils.buildImageUrl(500, mShownMovie.getPosterPath()))
                    .placeholder(R.drawable.movie_details_poster_placeholder)
                    .error(R.drawable.movie_details_poster_placeholder)
                    .fit().into(mMoviePosterImageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    mAddToFavoriteFloatingActionButton.setVisibility(View.VISIBLE);
                    mAddToFavoriteFloatingActionButton.animate().scaleX(1f).scaleY(1f).setDuration(100);
                }

                @Override
                public void onError() {

                }
            });

        }


        mMovieTitleTextView.setText(mShownMovie.getTitle());
        mMovieYearTextView.setText(mShownMovie.getReleaseDate());
        mMovieVoteRatingBar.setRating(mShownMovie.getVoteAverage().floatValue()/2f);
        mMovieOverviewTextView.setText(mShownMovie.getOverview());

        mMovieOverviewExpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMovieOverviewExpanded)
                {
                    mMovieOverviewTextView.setMaxLines(2);
                    mMovieOverviewExpandImageView.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    mMovieStarringTextView.setVisibility(View.GONE);
                    mMovieProducersTextView.setVisibility(View.GONE);
                    mMovieDirectorsTextView.setVisibility(View.GONE);
                }
            else
                {
                    mMovieOverviewTextView.setMaxLines(Integer.MAX_VALUE);
                    mMovieOverviewExpandImageView.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    if (!mMovieStarringTextView.getText().toString().isEmpty())
                    mMovieStarringTextView.setVisibility(View.VISIBLE);
                    if (!mMovieProducersTextView.getText().toString().isEmpty())
                        mMovieProducersTextView.setVisibility(View.VISIBLE);
                    if (!mMovieDirectorsTextView.getText().toString().isEmpty())
                        mMovieDirectorsTextView.setVisibility(View.VISIBLE);
                }
                mIsMovieOverviewExpanded=!mIsMovieOverviewExpanded;
            }
        });

        mAddToFavoriteFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFavoriteMovie=!mIsFavoriteMovie;
                if(mMoviePosterImageView.getDrawable() instanceof BitmapDrawable) {
                    if (mIsFavoriteMovie) {
                        addMovieToFavorites();
                        mAddToFavoriteFloatingActionButton.setImageResource(R.drawable.star_on);
                        Toast.makeText(mContext,"Added to favorites",Toast.LENGTH_SHORT).show();
                        notifyChange();
                    } else {
                        final String SELECTION = MovieContract.MovieEntry.MOVIE_ID + " = " + mShownMovie.getId();
                        mAddToFavoriteFloatingActionButton.setImageResource(R.drawable.star_off);
                        mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, SELECTION, null);
                        Toast.makeText(mContext,"Removed from favorites",Toast.LENGTH_SHORT).show();
                        notifyChange();
                    }
                }

            }
        });

    }

    private void notifyChange ()
    {
        if(getActivity() instanceof MainActivity)
        {

            int index = PreferenceManager.getDefaultSharedPreferences(mContext).getInt(getString(R.string.pref_sort_key),0);
            if(index == 4)
            ((MainActivity) getActivity()).selectSortType(index);
        }
    }

    private void addMovieToFavorites() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.MOVIE_ID, mShownMovie.getId());
        values.put(MovieContract.MovieEntry.MOVIE_TITLE, mShownMovie.getTitle());
        values.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, mShownMovie.getOverview());
        values.put(MovieContract.MovieEntry.MOVIE_DATE, mShownMovie.getReleaseDate());
        values.put(MovieContract.MovieEntry.MOVIE_POSTER, mShownMovie.getPosterPath());
        values.put(MovieContract.MovieEntry.MOVIE_RATE, mShownMovie.getVoteAverage());
        values.put(MovieContract.MovieEntry.MOVIE_COVER, mShownMovie.getBackdropPath());
        Bitmap bitmap = ((BitmapDrawable)mMoviePosterImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        byte[] image = outputStream.toByteArray();
        values.put(MovieContract.MovieEntry.MOVIE_POSTER_IMAGE,image);
        mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
    }


    @Override
    public void onResume() {
        super.onResume();
        setOnMovieClickListener((OnMovieClickedListener)getActivity());
    }

    private void updateTrailersList(){

        Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
        Call<TrailersResponse> call = apiService.getMovieTrailers(mShownMovie.getId(),Utils.API_KEY);
        call.enqueue(new Callback<TrailersResponse>() {
            @Override
            public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                TrailersResponse trailersResponse = response.body();
                if(trailersResponse!=null) {
                    List<Trailer> trailers = trailersResponse.getTrailers();
                    mTrailersAdapter.clear();
                    mTrailersAdapter.add(trailers);
                    if (trailers.size() > 0)
                        mTrailersTitleTextView.setVisibility(View.VISIBLE);
                }
                checkAllFetchFinish();
            }
            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {
                checkAllFetchFinish();
            }
        });

    }

    private void updateReviewsList ()
    {
        Utils.ApiEndPointsInterface apiService = Utils.getApiClient().create(Utils.ApiEndPointsInterface.class);
        Call<ReviewsResponse> call = apiService.getMovieReviews(mShownMovie.getId(),Utils.API_KEY);
        call.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                ReviewsResponse reviewsResponse = response.body();
                if(reviewsResponse!=null) {
                    List<Review> reviews = reviewsResponse.getResults();
                    mReviewsAdapter.clear();
                    mReviewsAdapter.add(reviews);
                    if (reviews.size() > 0)
                        mMovieReviewTitleTextView.setVisibility(View.VISIBLE);
                }
                checkAllFetchFinish();
            }
            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                checkAllFetchFinish();
            }
        });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewCompat.setTranslationY(mMovieCoverImageView, scrollY / 2);

        if (mActionBar != null) {
            if (scrollY > 255) {
                if(scrollY<306) {
                    mActionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(scrollY*5 % 255, 16,0,0)));
                    mActionBar.setTitle("");
                    if(scrollY>260)
                        mActionBar.setTitle(mShownMovie.getTitle());
                }
                else{
                    mActionBar.setTitle(mShownMovie.getTitle());
                    mActionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 16,0,0)));
                }
            }
            else {
                setActionBarStyleAtTop();
            }

        }
        position = scrollY;
    }


    private void setActionBarStyleAtTop()
    {
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(0, 16,0,0)));
        mActionBar.setTitle("");
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

    public void setOnMovieClickListener(OnMovieClickedListener onMovieClickedListener) {
        mOnSimilarMovieClickListener = onMovieClickedListener;
    }
}
