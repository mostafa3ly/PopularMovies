package com.example.mostafaaly.moviesapp.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;

import com.example.mostafaaly.moviesapp.data.MovieContract;
import com.example.mostafaaly.moviesapp.data.MovieDbHelper;
import com.example.mostafaaly.moviesapp.models.Movie;
import com.example.mostafaaly.moviesapp.models.MoviesResponse;
import com.example.mostafaaly.moviesapp.models.TrailersResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

    public static final String MOVIES_IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String MOVIES_DETAILS_BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;
    public static final String API_KEY = "8da7b20e982e4969bdb63ad657026c6c";
    public static final String TRAILERS_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    public static final String YOUTUBE_WATCH_TRAILERS_BASE_URI = "http://www.youtube.com/watch?v=";

    public static String buildImageUrl (int width, String imagePath)
    {
        return MOVIES_IMAGES_BASE_URL + "w" + width + imagePath;
    }

    public static int getImageWidth (int density)
    {
        if(density < DisplayMetrics.DENSITY_HIGH)
        {
            return 185;
        }
        else if(density < DisplayMetrics.DENSITY_XHIGH)
        {
            return 342;
        }
        else
        {
            return 500;
        }
    }

    public static String buildThumbnailUrl (String videoKey)
    {
        return TRAILERS_THUMBNAIL_BASE_URL + videoKey + "/0.jpg";
    }


    public static boolean isFavoriteMovie(String movieId, Context context)
    {
        SQLiteDatabase sqLiteDatabase = new MovieDbHelper(context).getWritableDatabase();
        final String[] PROJECTION = {
                MovieContract.MovieEntry.MOVIE_ID
        };
        final String SELECTION = MovieContract.MovieEntry.MOVIE_ID + " = " + movieId;
        Cursor cursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME,
                PROJECTION, SELECTION, null, null, null, null);
        if(cursor.moveToFirst() && cursor.getCount()!=0)
        {

            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public static Retrofit getApiClient()
    {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(MOVIES_DETAILS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface ApiEndPointsInterface
    {
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

    public static String getMovieGenresString (List<JsonObject> genres)
    {
        String genresStr = "( ";
        for (int i=0; i<genres.size(); i++) {

            genresStr  += genres.get(i).get("name").toString().replace("\"","");
            if(i!=genres.size()-1)
                genresStr += " , ";
        }
        genresStr += " )";
        return genresStr;
    }

    public static String getMovieCastString (JsonArray castList)
    {
        String castStr = "";
        for(int i=0; i<5; i++)
        {
            JsonObject actor = castList.get(i).getAsJsonObject();
            castStr += actor.get("name").toString().replace("\"","") + " (" + actor.get("character").toString().replace("\"","") + ") ";
            if(i<4)
                castStr+=" , ";
        }
        return castStr;
    }

    public static String getMovieCrewString (JsonArray crewList)
    {
        String crewStr = "";
        int i = 0;
        while(!crewList.get(i).getAsJsonObject().get("job").getAsString().equals("Director"))
        {
            i++;
        }
        crewStr += crewList.get(i).getAsJsonObject().get("name").toString().replace("\"","")
                +" ("+ crewList.get(i).getAsJsonObject().get("job").toString().replace("\"","")+")";
        return crewStr;
    }
}
