package com.wdong.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdong.R;
import com.wdong.adapter.SearchResultsAdapter;
import com.wdong.modal.Movie;
import com.wdong.network.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";


    @BindView(R.id.rv)
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    @BindView(R.id.search_keyword)
    EditText mKeyword;
    @BindView(R.id.search_description)
    TextView mDescription;
    @BindView(R.id.search_prev)
    Button mPrev;
    @BindView(R.id.search_next)
    Button mNext;

    int currPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mKeyword.clearFocus();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setLayoutManager(layoutManager);

//        Log.i(TAG, "Here");
        fetchSearchResults("", 1);
    }

    private void updateList(ArrayList<Movie> movies) {
        mAdapter = new SearchResultsAdapter(movies);
        recyclerView.setAdapter(mAdapter);
    }

    private void updateDescription(String description, boolean error) {
        if (error) {
            mDescription.setTextColor(getResources().getColor(R.color.colorRed));
        } else {
            mDescription.setTextColor(getResources().getColor(R.color.colorDefault));
        }
        mDescription.setText(description);
    }

    private void updateButtons(int pageNum, int totalPages) {
        // Log.i(TAG, "" + pageNum + " : " + totalPages);
        if (pageNum == 1) {
            mPrev.setVisibility(View.INVISIBLE);
        } else {
            mPrev.setVisibility(View.VISIBLE);
        }
        if (pageNum == totalPages || totalPages == 0) {
            mNext.setVisibility(View.INVISIBLE);
        } else {
            mNext.setVisibility(View.VISIBLE);
        }
        this.currPage = pageNum;
    }

    @OnClick({R.id.search_btn, R.id.search_prev, R.id.search_next})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                Log.i(TAG, "start to search title: " + mKeyword.getText().toString());
                if (TextUtils.isEmpty(mKeyword.getText().toString())) {
                    updateDescription(getString(R.string.search_empty_keyword), true);
                    mDescription.setTextColor(getResources().getColor(R.color.colorRed));
                } else {
                    fetchSearchResults(mKeyword.getText().toString(), 1);
                }
                break;
            case R.id.search_prev:
                Log.i(TAG, "Prev button is hit: " + (this.currPage - 1));
                fetchSearchResults(mKeyword.getText().toString(), this.currPage - 1);
                break;
            case R.id.search_next:
                Log.i(TAG, "Next button is hit: " + (this.currPage + 1));
                fetchSearchResults(mKeyword.getText().toString(), this.currPage + 1);
                break;
        }
    }

    private void fetchSearchResults(final String keyword, final int targetPage) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        String url;
        if ("".equals(keyword)) {
            url = String.format(Locale.US,
                    "http://13.52.165.20:8080/Fablix/api/movie/find?pageNum=%d&maxRecords=%d",
                     targetPage, 5);
        } else {
            url = String.format(Locale.US,
                    "http://13.52.165.20:8080/Fablix/api/movie/find?title=%s&pageNum=%d&maxRecords=%d",
                    keyword, targetPage, 5);
        }
//        String url = "http://10.0.2.2:8080/Fablix/api/movie/find";

        StringRequest movieRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                        try {
                            JSONObject json = new JSONObject(response);
//                            Log.i(TAG, response);
                            JSONObject data = json.getJSONObject("data");
                            JSONArray movies = data.getJSONArray("movies");

                            ArrayList<Movie> ret = new ArrayList<>();
                            for (int i = 0; i < movies.length(); i++) {
                                JSONObject jsonObject = movies.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setId(jsonObject.getString("id"));
                                movie.setTitle(jsonObject.getString("title"));
                                movie.setDirector(jsonObject.getString("director"));
                                movie.setYear(jsonObject.getInt("year"));

                                JSONArray genres = jsonObject.getJSONArray("genres");
                                ArrayList<String> gs = new ArrayList<>();
                                for (int j = 0; j < genres.length(); j++) {
                                    gs.add(genres.getJSONObject(j).getString("name"));
                                }
                                movie.setGenres(gs);

                                JSONArray stars = jsonObject.getJSONArray("stars");
                                ArrayList<String> ss = new ArrayList<>();
                                for (int j = 0; j < stars.length(); j++) {
                                    ss.add(stars.getJSONObject(j).getString("name"));
                                }
                                movie.setStars(ss);

                                ret.add(movie);
                            }
                            updateList(ret);
                            updateButtons(data.getInt("pageNum") + 1, data.getInt("totalPages"));
                            if (ret.size() == 0) {
                                updateDescription("No result found", false);
                            } else {
                                updateDescription(data.getString("searchDescription"), false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Something goes wrong");
            }
        });
        movieRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(movieRequest);
    }
}


