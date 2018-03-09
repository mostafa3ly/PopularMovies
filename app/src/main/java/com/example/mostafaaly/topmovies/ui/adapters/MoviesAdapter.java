package com.example.mostafaaly.topmovies.ui.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mostafaaly.topmovies.R;
import com.example.mostafaaly.topmovies.models.Movie;
import com.example.mostafaaly.topmovies.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mostafa Aly on 21/10/2016.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> moviesList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private int listItemLayoutResource;
    public MoviesAdapter(List<Movie> moviesList, Context context, OnItemClickListener onItemClickListener, int listItemLayoutResource) {
        this.moviesList = moviesList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.listItemLayoutResource = listItemLayoutResource;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.MoviesFragment_TextView_MovieTitle) TextView titleTextView;
        @BindView(R.id.MoviesFragment_ImageView_MoviePoster) ImageView posterImageView;
        @BindView(R.id.DetailsFragment_RatingBar_MovieRating) RatingBar voteRatingBar;
        @BindView(R.id.MoviesFragment_TextView_MovieYear) TextView yearTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        public void setOnItemClickListener(final Movie movie, final OnItemClickListener onItemClickListener)
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(movie);
                }
            });
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(listItemLayoutResource, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {

        Movie movie = moviesList.get(position);

        holder.titleTextView.setText(movie.getTitle());
        String year = movie.getReleaseDate();
        holder.yearTextView.setText(year);


        holder.voteRatingBar.setRating(movie.getVoteAverage().floatValue()/2f);

        if(Utils.checkNetworkConnection(context)) {
            int posterPlaceholder = listItemLayoutResource == R.layout.movies_list_item ? R.drawable.movie_poster_placeholder : R.drawable.similar_movies_poster_placeholder;
            int width = Utils.getImageWidth(context.getResources().getDisplayMetrics().densityDpi);
            Picasso.with(context).load(Utils.buildImageUrl(width, movie.getPosterPath()))
                    .placeholder(posterPlaceholder)
                    .error(posterPlaceholder)
                    .fit().into(holder.posterImageView);
        }
        else if (Utils.isFavoriteMovie(String.valueOf(movie.getId()),context))
        {
            holder.posterImageView.setImageBitmap(BitmapFactory.decodeByteArray(movie.getPosterImage(), 0, movie.getPosterImage().length));
        }
        else {
            holder.posterImageView.setImageResource(R.drawable.movie_poster_placeholder);
        }

        holder.setOnItemClickListener(movie,onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void clear ()
    {
        moviesList.clear();
        this.notifyDataSetChanged();
    }

    public void add (Movie movie)
    {
        moviesList.add(movie);
        notifyDataSetChanged();
    }

    public  void add (List<Movie> movies)
    {
        this.moviesList= movies;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onClick(Movie movie);
    }
}