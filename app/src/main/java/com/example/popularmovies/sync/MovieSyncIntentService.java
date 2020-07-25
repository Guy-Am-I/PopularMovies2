package com.example.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * class for handling async task requests in a service on seperate handler thread
 */
public class MovieSyncIntentService extends IntentService {

    /**
     * Default constructor to call IntentService's constructor
     */
    public MovieSyncIntentService(){
        super("MovieSyncIntentService");
    }

    /**
     * start the network task using the intent service
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MovieSyncTask.syncMovies(this);
    }
}
