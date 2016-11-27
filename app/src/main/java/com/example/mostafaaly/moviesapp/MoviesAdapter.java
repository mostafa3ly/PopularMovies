package com.example.mostafaaly.moviesapp;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mostafa Aly on 21/10/2016.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {



    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        super(context,0, movies);
    }

    /**
     *
     * @param position the index of the target item in the list
     * @param convertView the old view if found (null at first time creating the list item)
     * @param parent the view group carrying the items
     * @return rootView the view of each item in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        if(rootView == null) {
            rootView  = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Movie movie = getItem(position);
        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster_image_view);
        String moviePoster = movie.getMoviePoster();
        String url = "http://image.tmdb.org/t/p/w342/"+moviePoster;
        Picasso.with(getContext()).load(url).into(posterImageView);
        return rootView;
    }
}
