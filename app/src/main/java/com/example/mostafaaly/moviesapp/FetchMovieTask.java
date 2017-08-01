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
 * Created by mosta on 11/19/2016.
 */
public  class FetchMovieTask extends AsyncTask<String,Void,Movie[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;
    private MoviesAdapter mMoviesAdapter;


    public FetchMovieTask (Context context , MoviesAdapter moviesAdapter)
    {
        mContext = context;
        mMoviesAdapter = moviesAdapter;

    }

    /**
     * @param movieJsonStr the json string downloaded from moviedb website
     * @return list of movies fetched from moviedb website
     * @throws JSONException if there is any error in json parsing
     */
    private Movie[] getMovieDataFromJson(String movieJsonStr) throws JSONException {


        final String RESULTS = "results";
        final String RELEASE_DATE = "release_date";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String POSTER_PATH = "poster_path";
        final String MOVIE_ID = "id";

        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray moviesJsonArray = moviesJson.getJSONArray(RESULTS);

        Movie[] moviesList = new Movie[moviesJsonArray.length()];

        for (int i = 0; i < moviesJsonArray.length(); i++) {
            JSONObject movieResult = moviesJsonArray.getJSONObject(i);
            String overview;
            String date;
            String title;
            String poster;
            String rating;
            String id;

            overview = movieResult.getString(OVERVIEW);
            date = movieResult.getString(RELEASE_DATE);
            poster = movieResult.getString(POSTER_PATH);
            title = movieResult.getString(TITLE);
            rating = movieResult.getString(VOTE_AVERAGE);
            id = movieResult.getString(MOVIE_ID);
            moviesList[i] = new Movie(title, poster, overview, date, rating,id);

        }

        return moviesList;
    }


    @Override
    protected Movie[] doInBackground(String... params) {



        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        String sortType = params[0];


        try{


            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APPID_PARAM = "api_key";
            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon().
                    appendPath(sortType).appendQueryParameter(APPID_PARAM,"8da7b20e982e4969bdb63ad657026c6c").build(); //replace API-KEY with your api key
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

            return getMovieDataFromJson(moviesJsonStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }



    @Override
    protected void onPostExecute(Movie[] movies) {

        if (movies != null) {
            mMoviesAdapter.clear();
            for (Movie movie : movies) {
                mMoviesAdapter.add(movie);

            }
        }
    }

}