package com.example.popularmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/*
 * Class to assist in the database related management
 */
public class MovieDbContract {

    // Content AUthority is the name for the content provider - i.e. it has to be unique so as to
    // access our content (DB)
    public static final String CONTENT_AUTHORITY = "com.example.popularmovies";
    // This Uri will be used by anyone trying to get data from our app (DB)
    public static final Uri BASE_CONTENT_URI = Uri.parse("content:// " + CONTENT_AUTHORITY);

    //used in conkunction with the base Uri to reach a movie (any) and get its data
    public static final String PATH_MOVIE = "movie";


    /* class to define table contents of our movies table (in DB) */
    public static final class MovieEntry implements BaseColumns {

        //Uri to query for the movies table in our DB
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE).build();

        //Table name + labels
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "id"; //stored as int
        public static final String COLUMN_TITLE = "title"; //stored as string
        public static final String COLUMN_POSTER_PATH = "poster_path"; //stored as string
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path"; //stored as string
        public static final String COLUMN_SYNOPSIS = "synopsis"; //stored as string
        public static final String COLUMN_USER_RATING = "user_rating"; //stored as double
        public static final String COLUMN_POPULARITY = "popularity"; //stored as double
        public static final String COLUMN_RELEASE_DATE = "release_date"; //stored as string
        public static final String COLUMN_IS_FAV = "favorite"; //stored as int

        /**
         * Builds a uri used to query for a specific movie using its id
         * @param id the movie id we want to query for
         * @return the Uri used to query our DB for specific movie
         */
        public static Uri buildMovieUriWithId(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }


    }
}
