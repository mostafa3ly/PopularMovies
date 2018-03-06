package com.example.mostafaaly.topmovies.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mostafaaly.topmovies.R;
import com.example.mostafaaly.topmovies.models.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mosta on 6/3/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {


    private List<Review> reviews;
    private Context context;

    public ReviewsAdapter(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }



    public static class ReviewViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.DetailsFragment_TextView_ReviewAuthor)TextView authorTextView;
        @BindView(R.id.DetailsFragment_TextView_ReviewContent)TextView reviewTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }



    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {

        Review review = reviews.get(position);
        holder.authorTextView.setText(review.getAuthor());
        holder.reviewTextView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void clear ()
    {
        reviews.clear();
        this.notifyDataSetChanged();
    }

    public void add (Review review)
    {
        reviews.add(review);
        notifyDataSetChanged();
    }

    public  void add (List<Review> reviews)
    {
        this.reviews= reviews;
        notifyDataSetChanged();
    }
}
