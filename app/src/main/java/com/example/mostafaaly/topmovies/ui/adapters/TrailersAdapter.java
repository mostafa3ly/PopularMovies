package com.example.mostafaaly.topmovies.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mostafaaly.topmovies.R;
import com.example.mostafaaly.topmovies.models.Trailer;
import com.example.mostafaaly.topmovies.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mosta on 11/25/2016.
 */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {



    private List<Trailer> trailerList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public TrailersAdapter(List<Trailer> moviesList, Context context, OnItemClickListener onItemClickListener) {
        this.trailerList = moviesList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.DetailsFragment_ImageView_TrailerThumbnail)ImageView trailerImageView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        public void setOnItemClickListener(final Trailer trailer, final OnItemClickListener onItemClickListener)
        {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(trailer);
                }
            });
        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TrailerViewHolder holder, int position) {

        Trailer trailer = trailerList.get(position);
        Picasso.with(context).load(Utils.buildThumbnailUrl(trailer.getKey()))
                .placeholder(R.drawable.movie_trailer_placeholder)
                .error(R.drawable.movie_trailer_placeholder)
                .fit().into(holder.trailerImageView);
        holder.setOnItemClickListener(trailer,onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public void clear ()
    {
        trailerList.clear();
        this.notifyDataSetChanged();
    }

    public void add (Trailer trailer)
    {
        trailerList.add(trailer);
        notifyDataSetChanged();
    }

    public  void add (List<Trailer> trailers)
    {
        this.trailerList= trailers;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onClick(Trailer trailer);
    }
}
