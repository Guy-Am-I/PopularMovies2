package com.example.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.popularmovies.Data.MovieDbContract;

/**
 * class to perform actions related to synching/timing of our network data fetching
 */
public class MovieSyncUtils {

    private static boolean sInitialized;

    /**
     * Creates our sync tasks periodically (per job service - once a day)
     * checks if immediate sync is required, i.e. we have no data
     * @param context
     */
    synchronized public static void initialize(@NonNull final Context context) {

        //only perform one initialization per app lifetime
        if (sInitialized) return;

        sInitialized = true;

        /* Check contens of content provider to see if it has any data  on background thread */

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                Uri movieQueryUri = MovieDbContract.MovieEntry.CONTENT_URI;

                /* we query to check if empty (no need for data so we are fine with just getting
                 the id column for rows */
                String[] projection = {MovieDbContract.MovieEntry._ID};

                Cursor cursor = context.getContentResolver().query(
                        movieQueryUri,
                        projection,
                        null,
                        null,
                        null);

                //if we have no data to display either because db is empt or there was an error
                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }
                cursor.close();

            }
        });

        checkForEmpty.start();
    }

    /**
     * Method to perform a sync immediately using IntentService for asynchromous execution
     * @param context Context to be used to start intentService to sync
     */
    public static void startImmediateSync(@NonNull final Context context){
        Intent intentToSyncImmediately = new Intent(context, MovieSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
