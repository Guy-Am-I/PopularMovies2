package com.example.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;

import android.content.Intent;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.palette.graphics.Palette;


import com.example.popularmovies.Data.ExtraMovieData;
import com.example.popularmovies.Data.MovieDbContract;
import com.example.popularmovies.Utils.JsonUtils;
import com.example.popularmovies.Utils.NetworkUtils;
import com.example.popularmovies.databinding.MovieInfoBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    MovieInfoBinding mDetailBinding;
    private int movie_fav;
    private boolean appBarExpanded;
    private Menu collapsedMenu = null;
    public static final int MOVIE_DATA_LOADER_ID = 9999;

    private int movie_id;
    private String[] movie_video_ids;
    private String[][] review_data;

    public static final String[] MOVIE_DATA_PROJECTION = {
            MovieDbContract.MovieEntry.COLUMN_TITLE,
            MovieDbContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieDbContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieDbContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieDbContract.MovieEntry.COLUMN_USER_RATING,
            MovieDbContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieDbContract.MovieEntry.COLUMN_IS_FAV,
    };

    private static final int INDEX_MOVIE_TITLE = 0;
    private static final int INDEX_POSTER_PATH = 1;
    private static final int INDEX_BACKDROP_PATH = 2;
    private static final int INDEX_SYNOPSIS = 3;
    private static final int INDEX_USER_RATING = 4;
    private static final int INDEX_RELEASE_DATE = 5;
    private static final int INDEX_IS_FAV = 6;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_info);

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.movie_info);
        setSupportActionBar(mDetailBinding.animToolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent movie_that_started = getIntent();
        movie_id = movie_that_started.getIntExtra("id", 0);
        movie_fav = 0; //false

        mDetailBinding.movieDetailAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener(){
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //vertical offset = 0 means appBar fully expanded
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });

        LoaderManager.getInstance(this).initLoader(MOVIE_DATA_LOADER_ID, null, this);

        //perform network task to get reviews and videos
        URL videosURL = NetworkUtils.getUrlMovieVideos(this, movie_id);
        URL reviewsURL = NetworkUtils.getUrlMovieReviews(this, movie_id);
        new MovieDataAsyncTask().execute(videosURL, reviewsURL);



    }

    /**
     * Gets called by LoaderManager when new loader is created
     *
     * @param id   LoaderId for the loader that is being created
     * @param args any argumenets given to loader before loading
     * @return A newly created loader instance
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        Uri movieDataUri;

        switch (id) {
            case MOVIE_DATA_LOADER_ID:
                /* Get movie data when activity is launched */
                if (movie_id != 0) {
                    movieDataUri = MovieDbContract.MovieEntry.buildMovieUriWithId(movie_id);
                } else {
                    throw new UnsupportedOperationException("INvalud movie ID");
                }
                break;
            default:
                throw new RuntimeException("Issue creating loader: " + id);
        }
        //get all movies (just poster path & title)
        return new CursorLoader(this,
                movieDataUri,
                MOVIE_DATA_PROJECTION,
                null,
                null,
                null);
    }

    /**
     * Called when the loader has finished loading data
     *
     * @param loader The loader that loaded the data
     * @param data   said data generated by said loader
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //bind data to the UI
        data.moveToPosition(0); //only 1 row for this movie
        //Backdrop Poster
        String path = data.getString(INDEX_BACKDROP_PATH);
        String posterPath = data.getString(INDEX_POSTER_PATH);
        final URL imageURL = NetworkUtils.getUrlImage(getApplicationContext(), NetworkUtils.DEFAULT_BACKDROP_SIZE, path);
        URL posterURL = NetworkUtils.getUrlImage(getApplicationContext(), NetworkUtils.SMALL_POSTER_SIZE, posterPath);
        loadImagesIntoViews(imageURL, posterURL);
        /* RequestCreator picasso_data = Picasso.get().load(imageURL.toExternalForm());
        picasso_data.into(backdropPoster); */

        //mew thread for getting image from web and using pallete API
        Thread getBitmapThred = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap backdropBitmap = Picasso.get().load(imageURL.toExternalForm()).get();

                    Palette.from(backdropBitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(@Nullable Palette palette) {
                            int mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                            mDetailBinding.movieDetailCollapseToolbar.setContentScrimColor(mutedColor);
                        }
                    });

                }
                catch (Exception e){
                    Log.d("IMAGE RELATED", "Failed to fetch bitmap");
                    e.printStackTrace();
                }
            }
        });
        getBitmapThred.start();

        //title
        //mDetailBinding.movieDetailTitle.setText(data.getString(INDEX_MOVIE_TITLE));
        mDetailBinding.movieDetailCollapseToolbar.setTitle(data.getString(INDEX_MOVIE_TITLE));
        //rating
        String user_rating = data.getString(INDEX_USER_RATING);
        mDetailBinding.movieDetailRating.setText(user_rating);
        //release date
        mDetailBinding.movieDetailReleaseDate.setText(data.getString(INDEX_RELEASE_DATE));
        //synopsis
        mDetailBinding.movieDetailSynopsis.setText(data.getString(INDEX_SYNOPSIS));
        //update favorite
        int isFav = data.getInt(INDEX_IS_FAV);
        movie_fav = isFav;

        updateFloatingButtonIcon();
        //loadReviews();


    }
    public void loadImagesIntoViews(URL backdropURL, URL posterURL) {
        Picasso.get().load(posterURL.toExternalForm()).into(mDetailBinding.movieDetailPoster);

        Picasso.get().load(backdropURL.toExternalForm()).into(mDetailBinding.movieDetailBackdrop);

    }
    private void loadReviews() {
        String all_reviews = "";
        if (review_data != null)
        {
            for (int i = 0; i < review_data.length; i++) {

                Spanned text = Html.fromHtml("<h1>Author: " + review_data[i][0] + "</h1>"
                         +"<p>" + review_data[i][1] + "</p> ");

                all_reviews += text.toString();
            }
        }
        mDetailBinding.movieDetailReviews.setText(all_reviews);
    }

    /**
     * gets called when a created loader is being reset => its data will be unavailable
     *
     * @param loader The loader that is being reset
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //notify UI that data is unavailavle, i.e we need to clear the data

    }


    private class MovieDataAsyncTask extends AsyncTask<URL, Void, ExtraMovieData> {
        ExtraMovieData movieExtraData;

        @Override
        protected ExtraMovieData doInBackground(URL... params) {
            try {

                String videos_response = NetworkUtils.getResponseFromHttpUrl(params[0]);
                String[] video_ids = JsonUtils.getMovieVideosFromJSON(videos_response);

                String reviews_response = NetworkUtils.getResponseFromHttpUrl(params[1]);
                String[][] review_data = JsonUtils.getMovieReviewsFromJSON(reviews_response);

                movieExtraData = new ExtraMovieData(video_ids, review_data);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return movieExtraData;
        }

        @Override
        protected void onPostExecute(ExtraMovieData extraMovieData) {

            //update UI eventually
            movie_video_ids = extraMovieData.getVideo_ids();
            review_data = extraMovieData.getReviews();
            loadReviews();

        }
    }

    private void favoriteClicked()
    {
        //if it was favorite then un-favorite and vice versa
        movie_fav = movie_fav == 1 ? 0 : 1;
        updateFloatingButtonIcon();

        Uri movieIdUri = MovieDbContract.MovieEntry.buildMovieUriWithId(movie_id);
        ContentValues newFavValue = new ContentValues();
        newFavValue.put(MovieDbContract.MovieEntry.COLUMN_IS_FAV, movie_fav);

        ContentResolver movieDataContentResolver = this.getContentResolver();
        //Update function takes care of updaring only specific movie_id
        movieDataContentResolver.update(movieIdUri, newFavValue, null, null);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.movie_detail_fav: {
                favoriteClicked();
                break;
            }
            case R.id.movie_detail_vid1:
                watchYoutubeVideo(0);
                break;
            case R.id.movie_detail_vid2:
                watchYoutubeVideo(1);
                break;
            case R.id.movie_detail_vid3:
                watchYoutubeVideo(2);
                break;
            default:
                throw new UnsupportedOperationException("Error clicking button with no action");
        }
    }

    private void watchYoutubeVideo(int id_index) {
        int id_array_len = movie_video_ids.length;

        if (id_array_len > id_index) {
            String vid_id = movie_video_ids[id_index];
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + vid_id));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + vid_id));

            //open link in either youtube or web depending if app is installed
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);
            }
        }
        else {
            Toast.makeText(this, "No video found for movie", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFloatingButtonIcon() {
        if(movie_fav == 1) {
            mDetailBinding.movieDetailFav.setImageResource(R.drawable.ic_star_white_24dp);
        } else mDetailBinding.movieDetailFav.setImageResource(R.drawable.ic_star_border_white_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        collapsedMenu = menu;

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null && (!appBarExpanded || collapsedMenu.size() != 1)) {

            collapsedMenu.add("Add").setIcon(mDetailBinding.movieDetailFav.getDrawable())
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            //full image is expanded so we show default menu
        }

        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_go_to_settings:
                //go to settings activity
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.home:
                finish();
                return true;
        }
        if (item.getTitle() == "Add") {
            favoriteClicked();
        }

        return super.onOptionsItemSelected(item);
    }
}

