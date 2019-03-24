package com.wdong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.wdong.R;
import com.wdong.modal.Movie;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleMovieActivity extends AppCompatActivity {
    private final String TAG = "SingleMovieActivity";

    @BindView(R.id.single_movie_title)
    TextView mTitle;
    @BindView(R.id.single_movie_director)
    TextView mDirector;
    @BindView(R.id.single_movie_genres)
    TextView mGenres;
    @BindView(R.id.single_movie_stars)
    TextView mStars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("movie");
        mTitle.setText(String.format(Locale.US, "%s (%d)", movie.getTitle(), movie.getYear()));
        mDirector.setText(movie.getDirector());
        mGenres.setText( TextUtils.join(" | ", movie.getGenres()));
        mStars.setText(TextUtils.join(",  ", movie.getStars()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG, "Finish, go back");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
