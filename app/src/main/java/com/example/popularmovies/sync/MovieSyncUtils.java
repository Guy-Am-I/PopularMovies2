package com.example.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.popularmovies.Data.MovieDbContract;

import java.sql.Driver;
import java.util.concurrent.TimeUnit;

/**
 * class to perform actions related to synching/timing of our network data fetching
 */
public class MovieSyncUtils {

    private static final int FETCH_DATA_INTERVAL_HOURS = 24;
    private static final String FETCH_MOVIE_DATA_WORK_NAME = "FETCH_MOVIE_DATA";
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

        scheduleWorkerRequest(context);

        /* Check contens of content provider to see if it has any data  on background thread */

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                Uri movieQueryUri = MovieDbContract.MovieEntry.CONTENT_URI;

                /* we query to check if empty (no need for data so we are fine with just getting
                 the id */
                String[] projection = {MovieDbContract.MovieEntry.COLUMN_MOVIE_ID};

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
    static void scheduleWorkerRequest(@NonNull final Context context) {
        //set Data object here if we need to pass some input to the work

        //set constraints for the work to be done
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Creating the work Request
        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(UpdateMoviesWorker.class, FETCH_DATA_INTERVAL_HOURS, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(FETCH_MOVIE_DATA_WORK_NAME,
                        ExistingPeriodicWorkPolicy.KEEP, request);
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
