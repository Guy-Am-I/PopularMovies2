package com.example.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/* Class that serves as the content provicer for our app's data (only the movie info cached from
 * TheMovieDatabase
 * We use this class for inserting, deleting and query for data
 * NOTE: this is for learning purposes, in reality the only app to use our data is our own
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


    /**
     * handle the query action for whoever uses our content provider (database)
     * @param uri uri to query
     * @param projection list of columns we are looking for (null = all cols)
     * @param selection argument to apply if filtering rows (null = all rows)
     * @param selectionArgs if passing arguments in selection filter (each ? is substituted for relative arg
     * @param sortOrder how the rows should be sorted
     * @return a cursor pointing to first entry in the results (if null either error or no matching results)
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;
        //determine what kind of query is being made (using the uri matcher we created
        switch (sUriMatcher.match(uri)) {
            //case when we queried for a specific movie by its id
            case CODE_MOVIE_BY_ID: {
                //last 'argument' in uri is the id
                //ex: 'content://com.example.popularmovies/movie/<movie_id>
                String movie_id = uri.getLastPathSegment();

                //There can be many arguments that are being used to search (select) but in our case it is
                //only 1 = the movie id (for now)
                String[] selectionArguments = new String[]{movie_id};

                //get relevant data from database
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieDbContract.MovieEntry.TABLE_NAME,
                        /*
                         * projection is the list of columns (i.e movie data we want returned)
                         */
                        projection,
                        MovieDbContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            /*in the case when we did not query for a specific movie, i.e we want all the movies in
             * the database returned */
            case CODE_MOVIE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieDbContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                //failed to get correct code from uri matcher = uri was incorrctly built
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Delete a movie (data) at given uri
     * @param uri uri to query (where we delete)
     * @param selection optional parameters to specify what we will be deleting (rows)
     * @param selectionArgs used with selection to pass arguments into the string
     * @return number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rows_deleted;

        //passing null will delete all rows in DB
        //in order to know how many rows were deleted we pass "1" whcih returns that number
        //per SQLiteDatabase doc
        if (selection == null) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                rows_deleted = mOpenHelper.getWritableDatabase().delete(
                        MovieDbContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rows_deleted != 0) {
            //notify chagne in DB when we delete data from it to content resolver which handles comm with users
            getContext().getContentResolver().notifyChange(uri, null);
        }
         return rows_deleted;
    }

    /**
     * Returns the type of given data at uri
     * @param uri URI to query
     * @return a MIME type string or null if there is not type
     * NOTE: for now there is no complex data (like image, audio, video...) in our databse (only text, numbers) so
     * we do not implement this method
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("NO MIME TYPE IN POPULAR MOVIES");
    }

    //method to update certain rows of database
    //since our data is only cached from online API we do not update it (perhaps get data again and insert anew)
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("No update implementation, requery data from API and bulkInsert instead");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new RuntimeException("No insert implementation, use bulkInsert instead if needed");
    }
}
