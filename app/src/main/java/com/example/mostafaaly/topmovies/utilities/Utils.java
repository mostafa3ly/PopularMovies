package com.example.mostafaaly.topmovies.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;

import com.example.mostafaaly.topmovies.data.MovieContract;
import com.example.mostafaaly.topmovies.data.MovieDbHelper;
import com.example.mostafaaly.topmovies.models.Movie;
import com.example.mostafaaly.topmovies.models.MoviesResponse;
import com.example.mostafaaly.topmovies.models.TrailersResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mosta on 29/9/2017.
 */

public class Utils {

    private static final String MOVIES_IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String MOVIES_DETAILS_BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;
    public static final String API_KEY = "8da7b20e982e4969bdb63ad657026c6c";
    private static final String TRAILERS_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    public static final String YOUTUBE_WATCH_TRAILERS_BASE_URI = "http://www.youtube.com/watch?v=";

    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static String buildImageUrl(int width, String imagePath) {
        return MOVIES_IMAGES_BASE_URL + "w" + width + imagePath;
    }

    public static int getImageWidth(int density) {
        if (density < DisplayMetrics.DENSITY_HIGH) {
            return 185;
        } else if (density < DisplayMetrics.DENSITY_XHIGH) {
            return 342;
        } else {
            return 500;
        }
    }

    public static String buildThumbnailUrl(String videoKey) {
        return TRAILERS_THUMBNAIL_BASE_URL + videoKey + "/0.jpg";
    }


    public static boolean isFavoriteMovie(String movieId, Context context) {
        SQLiteDatabase sqLiteDatabase = new MovieDbHelper(context).getWritableDatabase();
        final String[] PROJECTION = {
                MovieContract.MovieEntry.MOVIE_ID
        };
        final String SELECTION = MovieContract.MovieEntry.MOVIE_ID + " = " + movieId;
        Cursor cursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                PROJECTION, SELECTION, null, null, null, null);
        if (cursor.moveToFirst() && cursor.getCount() != 0) {

            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public static Retrofit getApiClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(MOVIES_DETAILS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface ApiEndPointsInterface {
        @GET("movie/{sort_type}")
        Call<MoviesResponse> getMoviesList(@Path("sort_type") String sortType, @Query("api_key") String apiKey);

        @GET("movie/{id}")
        Call<Movie> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

        @GET("movie/{id}/videos")
        Call<TrailersResponse> getMovieTrailers(@Path("id") int id, @Query("api_key") String apiKey);

        @GET("movie/{id}/similar")
        Call<MoviesResponse> getSimilarMovies(@Path("id") int id, @Query("api_key") String apiKey);

        @GET("movie/{id}/credits")
        Call<JsonObject> getMovieCredits(@Path("id") int id, @Query("api_key") String apiKey);

    }

    public static String[] getMovieGenresNames(List<JsonObject> genres) {
        String[] genresNames = new String[genres.size()];
        for (int i = 0; i < genres.size(); i++) {
            genresNames[i] = genres.get(i).get("name").toString().replace("\"", "");
        }
        return genresNames;
    }

    public static SpannableString getSpannableMovieCreditString(String string, int end) {
        SpannableString spannableStr = new SpannableString(string);
        spannableStr.setSpan(new StyleSpan(Typeface.BOLD), 0, end, 0);
        spannableStr.setSpan(new ForegroundColorSpan(Color.BLACK), 0, end, 0);

        return spannableStr;
    }

    public static String getMovieStarringString(JsonArray castList) {
        StringBuilder castStr = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            try {
                JsonObject actor = castList.get(i).getAsJsonObject();
                castStr.append(actor.get("name").toString().replace("\"", ""));
                if (i < 4)
                    castStr.append(", ");
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        }
        return castStr.toString();
    }

    public static String getMovieProducersString(JsonArray crewList, String job) {
        StringBuilder producersStr = new StringBuilder();
        for (int i = 0; i < crewList.size(); i++) {
            JsonObject crewMember = crewList.get(i).getAsJsonObject();
            try {
                if (crewMember.get("job").toString().replace("\"", "").equals(job))
                    producersStr.append(crewMember.get("name").toString().replace("\"", "")).append(", ");
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        }
        String producers = producersStr.toString();
        if(producers.length()>2)
            producers = producers.substring(0,producers.length()-2);
        
        return producers;
    }


}
