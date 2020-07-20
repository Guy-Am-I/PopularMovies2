package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.popularmovies.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    final static int NUMBER_OF_MOVIES = 20;

    RecyclerView moviePostersRv;
    MovieAdapter movieAdapter;

    ActivityMainBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        moviePostersRv = mBinding.moviesRecyclerView;

        //Recycler View Initialization
        LinearLayoutManager moviePostersRvLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        moviePostersRv.setLayoutManager(moviePostersRvLayoutManager);
        moviePostersRv.setHasFixedSize(true);

        //Tie Adapter
        movieAdapter = new MovieAdapter(this, this);
        moviePostersRv.setAdapter(movieAdapter);

        //TODO add option to show loading data




    }

    /**
     * Gets called when a list item is clicked
     */
    @Override
    public void onClick() {

    }
    //TODO save info onSavedInstanceState when device is rotated
}
