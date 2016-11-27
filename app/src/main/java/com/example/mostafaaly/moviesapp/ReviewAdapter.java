package com.example.mostafaaly.moviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mosta on 11/25/2016.
 */
public class ReviewAdapter extends ArrayAdapter<Review>{
    public ReviewAdapter(Context context, ArrayList<Review> list) {
        super(context,0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        if(rootView == null) {
            rootView  = LayoutInflater.from(getContext()).inflate(
                    R.layout.review_list_item, parent, false);
        }

        Review review = getItem(position);
        TextView authorName = (TextView)rootView.findViewById(R.id.author_name);
        authorName.setText(review.getmAuthor());
        TextView content = (TextView)rootView.findViewById(R.id.review);
        content.setText(review.getmContent());
        return rootView;
    }
}
