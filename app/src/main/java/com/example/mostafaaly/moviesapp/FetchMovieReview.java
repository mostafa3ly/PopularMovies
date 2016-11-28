package com.example.mostafaaly.moviesapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mosta on 11/25/2016.
 */
public class FetchMovieReview extends AsyncTask<String,Void,Review[]> {


    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;
    private ReviewAdapter mReviewAdapter;

    public FetchMovieReview(Context context , ReviewAdapter reviewAdapter)
    {
        mContext = context;
        mReviewAdapter = reviewAdapter;

    }

    @Override
    protected Review[] doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        String movieId = params[0];

        try{


            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon().
                    appendPath(movieId).appendPath("reviews").appendQueryParameter(APPID_PARAM,"8da7b20e982e4969bdb63ad657026c6c").build();
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();


            if(inputStream ==  null)
            {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line ;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            moviesJsonStr = buffer.toString();


        }
        catch (IOException exception)
        {
            Log.e(LOG_TAG, "Error ", exception);
            return null;

        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }

            }

        }

        try {
            return getMovieTrailersFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param movieJsonStr the json string downloaded from moviedb website
     * @return  list of reviews of the movie
     * @throws JSONException if there is an error in json parsing
     */
    private Review[] getMovieTrailersFromJson(String movieJsonStr) throws JSONException
    {
        final String RESULTS = "results";
        final String CONTENT = "content";
        final String AUTHOR_NAME = "author";

        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray moviesJsonArray = moviesJson.getJSONArray(RESULTS);
        Review[] reviewsList = new Review[moviesJsonArray.length()];

        for (int i = 0; i < moviesJsonArray.length(); i++) {
            JSONObject movieResult = moviesJsonArray.getJSONObject(i);
            String key;
            String name;

            key = movieResult.getString(CONTENT);
            name = movieResult.getString(AUTHOR_NAME);
            reviewsList[i] = new Review(key, name);
        }
        return reviewsList;
    }



    @Override
    protected void onPostExecute(Review[] reviews) {
        if (reviews != null) {
            mReviewAdapter.clear();
            for (Review review : reviews) {
                mReviewAdapter.add(review);

            }
        }
    }
}
