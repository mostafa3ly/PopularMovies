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
public class FetchMovieTrailers extends AsyncTask<String,Void,Trailer[]> {


    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;
    private TrailersAdapter mTrailersAdapter;

    public FetchMovieTrailers(Context context , TrailersAdapter trailersAdapter)
    {
        mContext = context;
        mTrailersAdapter = trailersAdapter;

    }

    @Override
    protected Trailer[] doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        String movieId = params[0];

        try{


            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon().
                    appendPath(movieId).appendPath("videos").appendQueryParameter(APPID_PARAM,"8da7b20e982e4969bdb63ad657026c6c").build();
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

    private Trailer[] getMovieTrailersFromJson(String movieJsonStr) throws JSONException
    {

        final String RESULTS = "results";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";

        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray moviesJsonArray = moviesJson.getJSONArray(RESULTS);
        Trailer[] trailerList = new Trailer[moviesJsonArray.length()];
        for (int i = 0; i < moviesJsonArray.length(); i++) {
            JSONObject movieResult = moviesJsonArray.getJSONObject(i);
            String key;
            String name;

            key = movieResult.getString(TRAILER_KEY);
            name = movieResult.getString(TRAILER_NAME);
            trailerList[i] = new Trailer(key, name);
        }
        return trailerList;
    }


    @Override
    protected void onPostExecute(Trailer[] trailers) {
        if (trailers != null) {
            mTrailersAdapter.clear();
            for (Trailer trailer : trailers) {
                mTrailersAdapter.add(trailer);

            }
        }
    }
}
