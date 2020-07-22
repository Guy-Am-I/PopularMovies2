package com.example.popularmovies.Utils;

import android.content.ContentProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.popularmovies.Data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


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
    public static Movie[] getMoviesFromJSON(Context context, String moviesJSONString) throws JSONException {

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
        int number_of_movies = moviesJSONArray.length();

        //Movie array to represent movies parsed from JSON data
        Movie[] movies = new Movie[number_of_movies];

        //iterate over array and get each movie in order
        for (int i = 0; i < number_of_movies; i++) {
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

            Movie currentMovie = new Movie(id, title, posterPath, backdropPath, null, synopsis, null, user_rating, popularity, releaseDate, false);

            movies[i] = currentMovie;
        }

        return movies;
    }



}
