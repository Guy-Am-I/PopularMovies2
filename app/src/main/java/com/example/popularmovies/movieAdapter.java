package com.example.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private final Context mContext;
    private final MovieAdapterOnClickHandler movieAdapterOnClickHandler;

    /**
     * simple interface to handles calls to onCLick for each list item
     * can pass parameters to the activity that handles the onClick if needed
     */
    interface MovieAdapterOnClickHandler {
        void onClick(int movie_id);
    }

    //Cursor we use to access data in our SQLite database
    private Cursor mCursor;

    /**
     * Create new Movie Adapter
     * @param context context where it exists (used to connect to UI and app resources)
     * @param onClickHandler MovieAdapterOnClickHandler to handle calls to onCLick for each item
     */
    MovieAdapter(Context context, MovieAdapterOnClickHandler onClickHandler) {
        mContext = context;
        movieAdapterOnClickHandler = onClickHandler;
    }
    /**
     * Called each time a new view holder is created
     * this is where we inflate the layout onto screen
     * @param parent ViewGroup that this view holder is contained within
     * @param viewType useful for inflating different layouts for different items in recycler view
     * @return new MovieAdapterViewHolder that holds each view holder in the recycler view
     */
    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.movie_list_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new MovieAdapterViewHolder(view);
    }

    /**
     * this is called after the layout has been inititalized for the view Holder
     * this is where we add out data to the view (recycler view list item)
     * @param holder ViewHolder we are currently in to get access to layout items
     * @param position position in the recycler view list
     */
    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        //get relevant movie data
        mCursor.moveToPosition(position);

        /*
         * We only show movie poster image in the main recycler view
         */

        String posterPath = mCursor.getString(MainActivity.INDEX_MOVIE_POSTER_PATH);
        String title = mCursor.getString(MainActivity.INDEX_MOVIE_TITLE);
        //TODO get imageResource using Picasso

        //TODO delete once we have set retrieved movie data into the view
        //set movie image for each poster in the view holders
        holder.moviePoster.setImageResource(R.drawable.ic_launcher_foreground);
    }

    /**
     * get total items to display in the recycler view
     * @return amount of items
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /**
     * change the cursor our adapter is using when we load new data
     * (or switch pref on how to sort: pop/rating/fave)
     * we notify of change so the view can reload new data
     * @param newCursor the new cursor to be used as our adapter's data source
     */
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class to access movie_list_item layout items and handle onClick action
     */
    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView moviePoster;

        MovieAdapterViewHolder(View view) {
            super(view);

            //Bind view items
            moviePoster = (ImageView) view.findViewById(R.id.movie_list_item_IV);

            view.setOnClickListener(this);
        }

        /**
         * gets called when a movie poster is clicked
         * at which point we want to get its id so we can pass it to our Movie_Detail_Activity
         * and show relevant movie info using the onClick interface (that main activity is its handler)
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            mCursor.moveToPosition(adapterPosition);
            int movie_id = mCursor.getInt(MainActivity.INDEX_MOVIE_ID);
            movieAdapterOnClickHandler.onClick(movie_id);
        }
    }
}
