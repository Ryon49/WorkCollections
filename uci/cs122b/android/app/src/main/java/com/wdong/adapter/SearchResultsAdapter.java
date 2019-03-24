package com.wdong.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wdong.R;
import com.wdong.activity.SingleMovieActivity;
import com.wdong.modal.Movie;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private ArrayList<Movie> movies;

    public SearchResultsAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_card, viewGroup, false);
        return new ViewHolder(v, viewGroup.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsAdapter.ViewHolder viewHolder, int i) {
        Movie m = movies.get(i);
        viewHolder.mTitle.setText(m.getTitle());
        viewHolder.mDirector.setText(String.format(Locale.US,"Director:\t%s", m.getDirector()));
        viewHolder.mYear.setText(String.format(Locale.US,"Year:\t%d", m.getYear()));
        viewHolder.mGenres.setText(String.format(Locale.US,"Genres:\t%s", TextUtils.join(",  ", m.getGenres())));
        viewHolder.mStars.setText(String.format(Locale.US,"Stars:\t%s", TextUtils.join(",  ", m.getStars())));
        viewHolder.setMovie(m);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final int REQUEST_FOR_ACTIVITY_CODE = 1;

        @BindView(R.id.movie_title)
        TextView mTitle;
        @BindView(R.id.movie_director)
        TextView mDirector;
        @BindView(R.id.movie_year)
        TextView mYear;
        @BindView(R.id.movie_genres)
        TextView mGenres;
        @BindView(R.id.movie_stars)
        TextView mStars;

        private Context mContext;

        private Movie movie;

        public void setMovie(Movie movie) {
            this.movie = movie;

        }

        ViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.mContext = context;
        }

        @OnClick(R.id.movie_card)
        void click() {
            Intent intent = new Intent(mContext, SingleMovieActivity.class);
            intent.putExtra("movie", this.movie);
            ((Activity) mContext).startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);
        }
    }
}
