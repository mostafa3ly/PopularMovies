package com.example.mostafaaly.moviesapp;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by mosta on 11/9/2016.
 */
public class DetailsFragment extends Fragment {

    private final static String MOVIE_KEY = "Movie";
    private TrailersAdapter mTrailerAdapter;
    private ReviewAdapter mReviewsAdapter;
    private Movie movie;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details,container,false);
        Bundle sentBundle = getArguments();
         movie = (Movie)sentBundle.getSerializable(MOVIE_KEY);
        final ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.favorite_button);

        //projection of columns queried from db
        String[] projection = {
                MovieContract.MovieEntry.COLUMN_ID,
                MovieContract.MovieEntry.MOVIE_ID,
                MovieContract.MovieEntry.MOVIE_TITLE,
                MovieContract.MovieEntry.MOVIE_OVERVIEW,
                MovieContract.MovieEntry.MOVIE_RATE,
                MovieContract.MovieEntry.MOVIE_DATE,
                MovieContract.MovieEntry.MOVIE_POSTER
        };

        //query condition statement of comparing id of showed movie and queried movie
        final String SELECTION = MovieContract.MovieEntry.MOVIE_ID + " = " + movie.getMovieId();

        //this block of code see if the movie is already in favorite list to change its case and fav button
        MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase sqLiteDatabase = movieDbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                projection, SELECTION, null, null, null, null);
        if(cursor.getCount()!=0 && !movie.getFavoriteCase())
        {
            movie.changeFavoriteCase();
            imageButton.setImageResource(R.drawable.ic_star_yellow_48dp);
        }
        cursor.close();

        //this block of code handle the click on fav button and if user want to add or remove the movie to or from db
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());
                SQLiteDatabase sqLiteDatabase = movieDbHelper.getWritableDatabase();
                movie.changeFavoriteCase();
                if(movie.getFavoriteCase()) {
                    imageButton.setImageResource(R.drawable.ic_star_yellow_48dp);
                    try {
                        //insert the movie into db
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.MovieEntry.MOVIE_ID, movie.getMovieId());
                        values.put(MovieContract.MovieEntry.MOVIE_TITLE, movie.getMovieTitle());
                        values.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, movie.getMovieOverview());
                        values.put(MovieContract.MovieEntry.MOVIE_DATE, movie.getMovieDate());
                        values.put(MovieContract.MovieEntry.MOVIE_POSTER, movie.getMoviePoster());
                        values.put(MovieContract.MovieEntry.MOVIE_RATE, movie.getMovieRating());
                        long newId  = sqLiteDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                        Toast.makeText(getContext(),movie.getMovieTitle()+" was added to favorite list",Toast.LENGTH_SHORT).show();
                    }
                    catch (SQLException exception)
                    {
                        Log.i("Error","SQLite Error");
                    }
                }
                else
                {
                    //removing movie from db
                    imageButton.setImageResource(R.drawable.ic_star_border_black_48dp);
                    sqLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME,SELECTION,null);
                    Toast.makeText(getContext(),movie.getMovieTitle()+" was removed from favorite list",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //creating new custom array adapter object for listing reviews and trailers
        mTrailerAdapter = new TrailersAdapter(getActivity(),new ArrayList<Trailer>());
        mReviewsAdapter = new ReviewAdapter(getActivity(),new ArrayList<Review>());
        //linearlistview is a ready library added to make it easy to handle list view in scroll root view
        LinearListView trailersList = (LinearListView) rootView.findViewById(R.id.trailers_list);
        LinearListView reviewsList = (LinearListView) rootView.findViewById(R.id.reviews_list);
        trailersList.setAdapter(mTrailerAdapter);
        reviewsList.setAdapter(mReviewsAdapter);

        //this block of code create implicit intent to display movie trailers clicked by user
        trailersList.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                Trailer trailer = mTrailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getmKey()));
                startActivity(intent);
            }
        });

        addMovieDetailsToViews(rootView, movie);

        return rootView;
    }


    /**
     * set movie content to details layout page in their views
     *
     * @param rootView the root layout of the details layout
     * @param movie movie displayed in details page
     */
    private void addMovieDetailsToViews (View rootView,Movie movie){
        TextView textViewToUpdated = (TextView) rootView.findViewById(R.id.movie_title_text_view);
        textViewToUpdated.setText(movie.getMovieTitle());
        ImageView imageViewToUpdated = (ImageView) rootView.findViewById(R.id.movie_poster_image_view);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w342/"+movie.getMoviePoster()).into(imageViewToUpdated);
        textViewToUpdated = (TextView) rootView.findViewById(R.id.movie_date_text_view);
        textViewToUpdated.setText(movie.getMovieDate().substring(0,4));
        textViewToUpdated = (TextView) rootView.findViewById(R.id.movie_duration_text_view);
        textViewToUpdated.setText(Movie.TEMP_DURATION);
        textViewToUpdated = (TextView) rootView.findViewById(R.id.movie_rating_text_view);
        String rating = movie.getMovieRating()+"/"+ Movie.MAX_RATING;
        textViewToUpdated.setText(rating);
        textViewToUpdated = (TextView) rootView.findViewById(R.id.movie_overview_text_view);
        textViewToUpdated.setText(movie.getMovieOverview());
        ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.favorite_button);
        Log.i("button case",(movie.getFavoriteCase())?"true":"false");
        if(movie.getFavoriteCase())
        {
            imageButton.setImageResource(R.drawable.ic_star_yellow_48dp);

        }
        else {
            imageButton.setImageResource(R.drawable.ic_star_border_black_48dp);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateReviewsList();
        updateTrailersList();

    }


    /**
     * create fetchmovietrailers object and execute in background thread
     */
    private void updateTrailersList(){
        FetchMovieTrailers fetchMovieTrailers = new FetchMovieTrailers(getActivity(),mTrailerAdapter);
        fetchMovieTrailers.execute(movie.getMovieId());
    }

    /**
     * create fetchmoviereviews object and execute in background thread
     */
    private void updateReviewsList()
    {
        FetchMovieReview fetchMovieReview = new FetchMovieReview(getActivity(),mReviewsAdapter);
        fetchMovieReview.execute(movie.getMovieId());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
