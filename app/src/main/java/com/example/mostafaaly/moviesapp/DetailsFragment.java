package com.example.mostafaaly.moviesapp;


import android.content.ContentValues;
import android.content.Intent;
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
        final ImageButton imageButton = (ImageButton)rootView.findViewById(R.id.favorite_button);
         movie = (Movie)sentBundle.getSerializable(MOVIE_KEY);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                movie.changeFavoriteCase();
                if(movie.getFavoriteCase()) {
                    imageButton.setImageResource(R.drawable.ic_star_white_48dp);
                }
                else
                {
                    imageButton.setImageResource(R.drawable.ic_star_border_black_48dp);

                }
                Log.i("button case",(movie.getFavoriteCase())?"true":"false");
            }
        });
        mTrailerAdapter = new TrailersAdapter(getActivity(),new ArrayList<Trailer>());
        mReviewsAdapter = new ReviewAdapter(getActivity(),new ArrayList<Review>());
        LinearListView trailersList = (LinearListView) rootView.findViewById(R.id.trailers_list);
        LinearListView reviewsList = (LinearListView) rootView.findViewById(R.id.reviews_list);
        trailersList.setAdapter(mTrailerAdapter);
        reviewsList.setAdapter(mReviewsAdapter);
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
            imageButton.setImageResource(R.drawable.ic_star_white_48dp);

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

    private void updateTrailersList(){
        FetchMovieTrailers fetchMovieTrailers = new FetchMovieTrailers(getActivity(),mTrailerAdapter);
        fetchMovieTrailers.execute(movie.getMovieId());
    }

    private void updateReviewsList()
    {
        FetchMovieReview fetchMovieReview = new FetchMovieReview(getActivity(),mReviewsAdapter);
        fetchMovieReview.execute(movie.getMovieId());
    }
}
