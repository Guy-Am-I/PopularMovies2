package com.example.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.Data.ExtraMovieData;
import com.example.popularmovies.Data.MovieDbContract;
import com.example.popularmovies.Utils.JsonUtils;
import com.example.popularmovies.Utils.NetworkUtils;
import com.example.popularmovies.databinding.MovieDetailBinding;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    MovieDetailBinding mDetailBinding;
    private int movie_fav;
    public static final int MOVIE_DATA_LOADER_ID = 9999;

    private int movie_id;
    private String[] movie_video_ids;

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

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.movie_detail);

        Intent movie_that_started = getIntent();
        movie_id = movie_that_started.getIntExtra("id", 0);
        movie_fav = 0; //false

        LoaderManager.getInstance(this).initLoader(MOVIE_DATA_LOADER_ID, null, this);

        //perform network task to get reviews and videos
        URL videosURL = NetworkUtils.getUrlMovieVideos(this, movie_id);
        URL reviewsURL = NetworkUtils.getUrlMovieReviews(this, movie_id);
        new MovieDataAsyncTask().execute(videosURL, reviewsURL);


        mDetailBinding.movieDetailFav.setOnClickListener(this);


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
        ImageView backdropPoster = mDetailBinding.movieDetailBackdrop;
        String path = data.getString(INDEX_BACKDROP_PATH);
        URL imageURL = NetworkUtils.getUrlImage(this, NetworkUtils.DEFAULT_BACKDROP_SIZE, path);
        Picasso.get().load(imageURL.toExternalForm())
                .placeholder(R.drawable.ic_sync_black_40dp)
                .into(backdropPoster);

        //Main Movie Poster
        ImageView mainPoster = mDetailBinding.movieDetailPoster;
        String posterPath = data.getString(INDEX_POSTER_PATH);
        URL posterURL = NetworkUtils.getUrlImage(this, NetworkUtils.SMALL_POSTER_SIZE, posterPath);
        Picasso.get().load(posterURL.toExternalForm())
                .placeholder(R.drawable.ic_sync_black_40dp)
                .resize(70, 90)
                .centerInside()
                .into(mainPoster);

        //title
        mDetailBinding.movieDetailTitle.setText(data.getString(INDEX_MOVIE_TITLE));
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
        protected void onPreExecute() {
            //TODO show loading progress in videos & reviews Views
            super.onPreExecute();
        }

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

            //TODO update UI here (call appropiate function)
            //update UI eventually
            movie_video_ids = extraMovieData.getVideo_ids();
            String[][] review_data = extraMovieData.getReviews();

            for (int i = 0; i < review_data.length; i++) {
                Log.d("DETAIL", "author: " + review_data[i][0] + "  content: " + review_data[i][1]);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.movie_detail_fav: {
                //if it was favorite then un-favorite and vice versa
                movie_fav = movie_fav == 1 ? 0 : 1;
                updateFloatingButtonIcon();

                Uri movieIdUri = MovieDbContract.MovieEntry.buildMovieUriWithId(movie_id);
                ContentValues newFavValue = new ContentValues();
                newFavValue.put(MovieDbContract.MovieEntry.COLUMN_IS_FAV, movie_fav);

                ContentResolver movieDataContentResolver = this.getContentResolver();
                //Update function takes care of updaring only specific movie_id
                movieDataContentResolver.update(movieIdUri, newFavValue, null, null);
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
}

