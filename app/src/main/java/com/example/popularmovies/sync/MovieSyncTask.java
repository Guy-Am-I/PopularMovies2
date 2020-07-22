package com.example.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.popularmovies.Data.MovieDbContract;
import com.example.popularmovies.Utils.JsonUtils;
import com.example.popularmovies.Utils.NetworkUtils;

import java.net.URL;

/**
 * Class we use to fetch movie data from online
 * we make sure it is in sync since we do this once daily using jobService
 */
public class MovieSyncTask {

    /**
     * Performs network request for movie data, parses the JSON data and inserts into our DB
     * (Content Provider)
     * In addition we will notify user of updated movie data if he hasn't been notified today
     * and has notification enabled
     * @param context
     */
    synchronized public static void syncMovies(Context context) {

        URL movieRequestPopularUrl = NetworkUtils.getUrlPopular(context);
        URL movieRequestTopRatedUrl = NetworkUtils.getUrlTopRated(context);
        ContentValues[] popular_movie_values = getMovieData(context, movieRequestPopularUrl);
        ContentValues[] top_rated_movie_values = getMovieData(context, movieRequestTopRatedUrl)

        if ((popular_movie_values != null && popular_movie_values.length != 0)
                || (top_rated_movie_values != null && top_rated_movie_values.length != 0)){

            /* content resolver used to mangage data in db */
            ContentResolver popularmoviesContentResolver = context.getContentResolver();

            /*delete old data */
            popularmoviesContentResolver.delete(MovieDbContract.MovieEntry.CONTENT_URI, null, null);

            /* insert newly received values */
            popularmoviesContentResolver.bulkInsert(MovieDbContract.MovieEntry.CONTENT_URI, popular_movie_values);
            popularmoviesContentResolver.bulkInsert(MovieDbContract.MovieEntry.CONTENT_URI, top_rated_movie_values);

            //TODO add notification capability per function docstring description
        }
    }

    private static ContentValues[] getMovieData(Context context, URL url_query) {

        try {
            /* get json data */
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(url_query);
            /* parse json data into content values to be cached in our db */
            ContentValues[] movieValues = JsonUtils.getMoviesFromJSON(context, jsonMovieResponse);

            return movieValues;

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
