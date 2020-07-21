package com.example.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/* Class that serves as the content provicer for our app's data (only the movie info cached from
 * TheMovieDatabase
 * We use this class for inserting, deleting and query for data
 */
public class MovieProvider extends ContentProvider {

    //Unique id's to compare when looking for data in our databsee
    //either by movie (any), or by movie with ID (specific movie)
    public static final int CODE_MOVIE = 979;
    public static final int CODE_MOVIE_BY_ID = 9797;

    //our URI matcher used to match the query code above to its appropiate Uri for querying databse
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        //initializing a new uri matcher with code NO_MATCH which signals the root URI
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String contentAuthority = MovieDbContract.CONTENT_AUTHORITY;

        //URI's for getting content from out database
        //'#' symbol signifies to use CODE_MOVIE_BY_ID if a number exists at the end (i.e. we added an id to the query)
        //ex query uri: content://com.example.popularmovies/movie/<empty or movie_id>
        uriMatcher.addURI(contentAuthority, MovieDbContract.PATH_MOVIE, CODE_MOVIE);
        uriMatcher.addURI(contentAuthority, MovieDbContract.PATH_MOVIE + "/#", CODE_MOVIE_BY_ID);

        return uriMatcher;
    }

    /**
     * Load application's content provider
     * NOTE this method gets called for every app that has a content provider registered on app startup
     * so we want to make sure to not consume time with operations (especially relating to querying db)
     * to avoid freezing/delaying app launch
     * @return true if provider was successfully loaded
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /**
     * handles inserting a lot of data into our DB - we use this to insert all the movies we fetch
     * from TheMovieDB into our db.
     * @param uri The part of the uri before we append insert query
     * @param values An array of column/value pairs to add to our database (label + relevant data)
     * @return Number of new values we inserted
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        //our database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch(sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                //"open" connection to our db
                db.beginTransaction();
                int rows_inserted = 0;

                try {
                    //go over each value (movie) given
                    for (ContentValues value : values) {
                        //insert our data
                        long _id = db.insert(MovieDbContract.MovieEntry.TABLE_NAME, null, value);
                        //check if successfull
                        if (_id != -1) rows_inserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    //close connection
                    db.endTransaction();
                }
                //added new data to our databse so we can notify COntentResolver => which will notify whoever is trying
                //to acces data in turn
                if (rows_inserted > 0) getContext().getContentResolver().notifyChange(uri, null);

                return rows_inserted;


                default:
                    return super.bulkInsert(uri, values);
        }
    }



    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @Nullable CancellationSignal cancellationSignal) {
        return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
