package com.example.popularmovies.Utils;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/*
 * utilities to be used when trying to get data from server
 */
public final class NetworkUtils {

    private static String API_KEY;
    private final static String TAG = NetworkUtils.class.getSimpleName();

    private static String BASE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private static String TOP_RATED_QUERY = "top_rated";
    private static String POPULAR_QUERY = "popular";
    private static String API_QUERY = "api_key";

    private static String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    public static String GRID_DEFAULT_POSTER_SIZE_PORT = "w500";
    public static String SMALL_POSTER_SIZE = "w185";
    public static String GRID_DEFAULT_POSTER_SIZE_LAND = "w500";
    public static String GRID_DEFAULT_POSTER_SIZE_LAND_TABLET = "w780";
    public static String DEFAULT_BACKDROP_SIZE = "original";

    /*
     * get API key from resources - for that we need context
     */
    private static String getApiKey(Context context) {
        return context.getResources().getString(R.string.TheMovieDbApiKey);
    }

    /*
     * Methods for creating Uri using base_url and different querys per TheMovieDB Api doc
     */

    private static Uri buildMovieUrlWithPopular() {
        String popularBasePath = BASE_MOVIE_URL + POPULAR_QUERY;
        Uri movieQueryUri = Uri.parse(popularBasePath).buildUpon()
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();

        return movieQueryUri;
    }
    private static Uri buildMovieUrlWithTopRated() {
        String topRatedBasePath = BASE_MOVIE_URL + TOP_RATED_QUERY;
        Uri movieQueryUri = Uri.parse(topRatedBasePath).buildUpon()
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();
        return movieQueryUri;
    }

    private static Uri buildImageUri(String file_size, String file_path) {
        String imagePath = BASE_IMAGE_URL + file_size + "/" + file_path;
        Uri imageQueryUri = Uri.parse(imagePath).buildUpon().build();

        return imageQueryUri;
    }

    /*
     * use given uri to create a URL to query server
     * returns null if failed to create proper url
     */
    private static URL createURL(Uri uri) {
        try {
            URL url = new URL(uri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get url to query for movies sorting by most popular
     * @return the appropiate url
     */
    public static URL getUrlPopular(Context context) {
        API_KEY = getApiKey(context);
        Uri popularUri = buildMovieUrlWithPopular();
        return createURL(popularUri);
    }

    /**
     * get url to query for movies sorted by rating
     * @return the appropiate url
     */
    public static URL getUrlTopRated(Context context) {
        API_KEY = getApiKey(context);
        Uri topRatedUri = buildMovieUrlWithTopRated();
        return createURL(topRatedUri);
    }

    /**
     * get url to query for an image from TheMovieDB
     * @param file_size size of the image (one of): "w92", "w154", "w185", "w342", "w500", "w780", or "original"
     * @param file_path path to image file
     * @return
     */
    public static URL getUrlImage(Context context, String file_size, String file_path) {
        API_KEY = getApiKey(context);
        Uri imageQueryUri = buildImageUri(file_size, file_path);
        return createURL(imageQueryUri);
    }


    /**
     * returns result from the HTTP response (to our server query)
     * @param url url to query that gets the response
     * @return the contents of the HTTP response, NULL if empty
     * @throws IOException if there was a connection/server error
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        //first open connection to the server
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            //use scanner to check response
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;

            //get data
            if (hasInput) response = scanner.next();

            scanner.close();

            return response;
        } finally {
            //close connection to server
            urlConnection.disconnect();
        }
    }
}
