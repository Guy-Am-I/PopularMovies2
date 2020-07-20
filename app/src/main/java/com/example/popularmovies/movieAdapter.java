package com.example.popularmovies;

import android.app.Activity;
import android.content.Context;
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
        void onClick();
    }

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
        //set movie image for each poster in the view holders
        holder.moviePoster.setImageResource(R.drawable.ic_launcher_foreground);
    }

    /**
     * get total items to display in the recycler view
     * @return amount of items
     */
    @Override
    public int getItemCount() {
        return MainActivity.NUMBER_OF_MOVIES;
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

        @Override
        public void onClick(View v) {

        }
    }
}
