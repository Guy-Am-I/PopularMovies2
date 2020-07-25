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

    //JSON query strings to get movie per TheMovieDB api
    static final String TMD_MOVIES_CODE = "results";
    static final String TMD_ID = "id";
    static final String TMD_TITLE = "title";
    static final String TMD_POSTER_PATH = "poster_path";
    static final String TMD_BACKDROP_PATH = "backdrop_path";
    static final String TMD_SYNOPSIS = "overview";
    static final String TMD_USER_RATING = "vote_average";
    static final String TMD_POPULARITY = "popularity";
    static final String TMD_RELEASE_DATE = "release_date";
    static final String TMD_REVIEW_AUTHOR = "author";
    static final String TMD_REVIEW_CONTENT = "content";

    /**
     * parse JSON string data into Movie objects
     * @param moviesJSONString String containing JSON data
     * @return  Movie array containing all elements from JSON (in order of appearance)
     * @throws JSONException if there was an error parsing data
     */
    public static ContentValues[] getMoviesFromJSON(String moviesJSONString) throws JSONException {

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
            //initially not favorite
            movieValues.put(MovieEntry.COLUMN_IS_FAV, 0); //0 = false, 1 = true

            movies[i] = movieValues;
        }

        return movies;
    }

    /**
     * get a String array representing youtube_video_id's for given movie from TheMovieDB
     * option to retrieve a lot more data regarding each video if needed
     * @param jsonString String returned from TheMovieDB query
     * @return a string array of video id's relating to movie
     * @throws JSONException if there was an error parsing data
     */
    public static String[] getMovieVideosFromJSON (String jsonString) throws JSONException {

        //holds all data from the string
        JSONObject moviesJSON = new JSONObject(jsonString);

        JSONArray movieVideosJSONArray = moviesJSON.getJSONArray(TMD_MOVIES_CODE);

        int number_of_videos = movieVideosJSONArray.length();

        String[] videos_id = new String[number_of_videos];

        for (int i = 0; i < number_of_videos; i++){

            JSONObject video_detail = movieVideosJSONArray.getJSONObject(i);
            //only get the video id for now, afterwards we can add more data relating to the video if needed
            String video_id = video_detail.getString(TMD_ID);

            videos_id[i] = video_id;
        }

        return videos_id;
    }

    /**
     * get a 2D string array representing the movie's reviews. each row in array will have 2 values:
     * author:review
     * @param jsonString String returned from TheMovieDB query
     * @return a 2D string array of author:review pairs
     * @throws JSONException if there was an error parsing data
     */
    public static String[][] getMovieReviewsFromJSON(String jsonString) throws JSONException {

        JSONObject moviesJSON = new JSONObject(jsonString);
        JSONArray movieReviewsArray = moviesJSON.getJSONArray(TMD_MOVIES_CODE);

        int number_of_reviews = movieReviewsArray.length();

        String[][] movieReviewPairs = new String[number_of_reviews][2];

        for (int i = 0; i < number_of_reviews; i++){

            JSONObject reviews = movieReviewsArray.getJSONObject(i);

            String author = reviews.getString(TMD_REVIEW_AUTHOR);
            String review = reviews.getString(TMD_REVIEW_CONTENT);

            movieReviewPairs[i][0] = author;
            movieReviewPairs[i][1] = review;
        }

        return movieReviewPairs;
    }



}
