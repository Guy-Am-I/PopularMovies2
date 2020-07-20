package com.example.popularmovies.Utils;


import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/*
 * utilities to be used when trying to get data from server
 */
public final class NetworkUtils {

    /**
     * get url to query for data from TheMovieDatabase.org
     * @return the url to query
     */
    public static URL getUrl() {

    }

    /**
     * get url to query for movies sorting by most popular
     * @return the appropiate url
     */
    public static URL getUrlPopular() {

    }

    /**
     * get url to query for movies sorted by rating
     * @return the appropiate url
     */
    public static URL getUrlTopRated() {

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
