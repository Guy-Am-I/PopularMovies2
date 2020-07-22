package com.example.popularmovies.Utils;

import android.content.ContentValues;
import android.content.Context;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.popularmovies.Data.MovieDbContract.MovieEntry;


/*
 * Utility class to handle parsing JSON data from per TheMovieDB
 */
public final class JsonUtils {

    /**
     * parse JSON string data into Movie objects
     * @param moviesJSONString String containing JSON data
     * @return  Movie array containing all elements from JSON (in order of appearance)
     * @throws JSONException if there was an error parsing data
     */
    public static ContentValues[] getMoviesFromJSON(Context context, String moviesJSONString) throws JSONException {

        //JSON query strings to get movie per TheMovieDB api
        final String TMD_MOVIES_CODE = "results";
        final String TMD_ID = "id";
        final String TMD_TITLE = "title";
        final String TMD_POSTER_PATH = "poster_path";
        final String TMD_BACKDROP_PATH = "backdrop_path";
        final String TMD_SYNOPSIS = "overview";
        final String TMD_USER_RATING = "vote_average";
        final String TMD_POPULARITY = "popularity";
        final String TMD_RELEASE_DATE = "release_date";

        //holds all data from the string
        JSONObject moviesJSON = new JSONObject(moviesJSONString);

        JSONArray moviesJSONArray = moviesJSON.getJSONArray(TMD_MOVIES_CODE);
        //!API supports a lot more movies in several pages - we just return first page for now
        int number_of_movies_in_page = moviesJSONArray.length();

        //Movie array to represent movies parsed from JSON data
        ContentValues[] movies = new ContentValues[number_of_movies_in_page];

        //iterate over array and get each movie in order
        for (int i = 0; i < number_of_movies_in_page; i++) {
            //current object in array representing movie
            JSONObject movieJSON = moviesJSONArray.getJSONObject(i);

            int id = movieJSON.getInt(TMD_ID);
            String title = movieJSON.getString(TMD_TITLE);
            String posterPath = movieJSON.getString(TMD_POSTER_PATH);
            String backdropPath = movieJSON.getString(TMD_BACKDROP_PATH);
            String synopsis = movieJSON.getString(TMD_SYNOPSIS);
            String releaseDate = movieJSON.getString(TMD_RELEASE_DATE);
            double user_rating = movieJSON.getDouble(TMD_USER_RATING);
            double popularity = movieJSON.getDouble(TMD_POPULARITY);


            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
            movieValues.put(MovieEntry.COLUMN_SYNOPSIS, synopsis);
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieEntry.COLUMN_USER_RATING, user_rating);
            movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);

            movies[i] = movieValues;
        }

        return movies;
    }



}
