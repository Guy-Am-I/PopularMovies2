package com.example.popularmovies.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class UpdateMoviesWorker extends Worker {

    public UpdateMoviesWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        MovieSyncTask.syncMovies(context);
        /*
        mFetchMovieDataTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                MovieSyncTask.syncMovies(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        };

        mFetchMovieDataTask.execute();*/
        return Result.success();

    }
}
