package com.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.example.popularmovies.Data.MovieDbContract;
import com.example.popularmovies.Utils.NotificationUtils;
import com.example.popularmovies.databinding.ActivityMainBinding;
import com.example.popularmovies.sync.MovieSyncUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private final String TAG = MainActivity.class.getSimpleName();

    final static int NUMBER_OF_MOVIES = 20;

    /*
     * Column Projection for the data we want to show in the main activity
     * We only present the movie poster image (which leads to the Move_detail_activity)
     * We also get the tile in case we couldn't show image for some reason (most commonly network since
     * the image is not stored in our DB - just the path to its location online)
     */
    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieDbContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieDbContract.MovieEntry.COLUMN_TITLE,
            MovieDbContract.MovieEntry.COLUMN_POSTER_PATH,
    };


    //Indicies of above array (Make sure they are correct)
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;

    //we only have one loader for out one DB but nonethelss we want a unique ID for good practice
    public static final int MOVIE_LOADER_ID = 77777;

    private int mPosition = RecyclerView.NO_POSITION;
    private RecyclerView moviePostersRv;
    private MovieAdapter movieAdapter;
    public BottomNavigationView bot_nav;

    ActivityMainBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        moviePostersRv = mBinding.moviesRecyclerView;
        bot_nav = mBinding.botNav;
        //setSupportActionBar(mBinding.mainActivityToolbar);

        NotificationUtils.createNotificationChannel(this);


        //Recycler View Initialization
        GridLayoutManager moviePostersGridManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        moviePostersRv.setLayoutManager(moviePostersGridManager);

        moviePostersRv.setHasFixedSize(true);

        //Tie Adapter
        movieAdapter = new MovieAdapter(this, this);
        moviePostersRv.setAdapter(movieAdapter);



        //initialize loader with our id, and this activity (mainActivity) as the callback handler
        LoaderManager.getInstance(this).initLoader(MOVIE_LOADER_ID, null, this);


        MovieSyncUtils.initialize(this);

        //set navigation listener
        bot_nav.setOnNavigationItemSelectedListener(this);
    }

    /**
     * Gets called when a list item is clicked
     */
    @Override
    public void onClick(int movie_id) {
        //Start Movie_Detail_Activity, passing it our movie_id so it can fetch relevant data

        //create intent
        Intent goToMovieDetail = new Intent (this, MovieDetailActivity.class);
        //insert movie details
        goToMovieDetail.putExtra("id", movie_id);
        startActivity(goToMovieDetail);
    }
    //TODO save info onSavedInstanceState when device is rotated

    /**
     * Gets called by LoaderManager when new loader is created
     * @param id LoaderId for the loader that is being created
     * @param args any argumenets given to loader before loading
     * @return A newly created loader instance
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        //get all rows of our movie data
        Uri movieQueryUri = MovieDbContract.MovieEntry.CONTENT_URI;;
        String[] projection = MAIN_MOVIE_PROJECTION;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder;

        switch (id) {
            case MOVIE_LOADER_ID:
                if (args == null) {
                    /* Sort by popularity when app is launched */
                    sortOrder = MovieDbContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                }
                else {
                    movieQueryUri = args.getParcelable("uri");
                    projection = args.getStringArray("projection");
                    selection = args.getString("selection");
                    selectionArgs = args.getStringArray("selectionArgs");
                    sortOrder = args.getString("sortOrder");
                }
                break;
            default:
                throw new RuntimeException("Issue creating loader: " + id);
        }
        //get all movies (just poster path & title)
        return new CursorLoader(this,
                movieQueryUri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    /**
     * Called when the loader has finished loading data
     * @param loader The loader that loaded the data
     * @param data said data generated by said loader
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
        //in case it is called when activity launched than we go back to the top (beginning)
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        moviePostersRv.smoothScrollToPosition(mPosition);

        if(data.getCount() != 0) {
            //we have data to show, can use loading view while loading and replace it here
        }
    }

    /**
     * gets called when a created loader is being reset => its data will be unavailable
     * @param loader The loader that is being reset
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        //notify recycler view adapter that data is unavailavle, i.e we need to clear the data
        movieAdapter.swapCursor(null);
    }

    /**
     * Perform different action based on what item was clicked
     * @param item Choice in bot nav view (popular, favorites, top_rated)
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        refreshDataForChosenNavItem(item.getItemId());
        return true;

    }
    public void refreshDataForChosenNavItem(int itemID) {
        String selection = null;
        String sortOrder = null;

        switch(itemID) {
            case R.id.bot_nav_popular:
                //get sorted data by popularity in DB
                sortOrder = MovieDbContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                break;
            case R.id.bot_nav_top_rated:
                //get sorted data by rating in DB
                sortOrder = MovieDbContract.MovieEntry.COLUMN_USER_RATING + " DESC";
                break;
            case R.id.bot_nav_favorites:
                //get all data that are favorited in DB
                selection = MovieDbContract.MovieEntry.COLUMN_IS_FAV + " = 1 ";
                break;
            //should never reach here!!
            default:
                throw new UnsupportedOperationException("Error getting id bot_nav clicked");
        }

        Bundle query_data = createLoaderBundle(selection, null, sortOrder);

        LoaderManager.getInstance(this).restartLoader(MOVIE_LOADER_ID, query_data, this);
    }

    /**
     * create a bundle with data to give the cursos which will query the Contetn Provider (SQL),
     * using main content_uri & MAIN_MOVIE_PROJECTION as defeined in thus class
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    public static Bundle createLoaderBundle(String selection, String[] selectionArgs, String sortOrder) {
        Bundle query_data = new Bundle();
        query_data.putParcelable("uri", MovieDbContract.MovieEntry.CONTENT_URI);
        query_data.putStringArray("projection", MAIN_MOVIE_PROJECTION);
        query_data.putString("selection", selection);
        query_data.putStringArray("selectionArgs", selectionArgs);
        query_data.putString("sortOrder", sortOrder);

        return query_data;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_go_to_settings) {
            //go to settings activity
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
