package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.popularmovies.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    RecyclerView moviePostersRv;

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

        //Bind rv to layout



    }

    //TODO save info onSavedInstanceState when device is rotated
}
