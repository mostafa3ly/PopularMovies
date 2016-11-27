package com.example.mostafaaly.moviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mosta on 11/25/2016.
 */
public class TrailersAdapter extends ArrayAdapter<Trailer>{

    public TrailersAdapter(Context context, ArrayList<Trailer> list) {
        super(context,0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        if(rootView == null) {
            rootView  = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailer_list_item, parent, false);
        }
        Trailer trailer = getItem(position);
        TextView trailerName = (TextView)rootView.findViewById(R.id.trailer_name);
        trailerName.setText(trailer.getmName());
        return rootView;
    }
}
