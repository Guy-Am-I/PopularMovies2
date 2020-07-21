package com.example.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.popularmovies.Data.MovieDbContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    //Databse name and version for our SQLite database
    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 3;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Initial creation of databse (where we add the tables / column labels
     * @param db the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        //String to create a movies table with the following columns for storing data for each movie

        final String SQL_CREATE_MOVIES_TABLE =
        "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + "INTEGER NOT NULL, "   +
                MovieEntry.COLUMN_TITLE + "TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + "TEXT NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_PATH  + "TEXT NOT NULL, " +
                MovieEntry.COLUMN_SYNOPSIS + "TEXT NOT NULL, " +
                MovieEntry.COLUMN_USER_RATING + "REAL NOT NULL, " +
                MovieEntry.COLUMN_IS_FAV + "INTEGER NOT NULL, " +

                //a movie id is unique to each movie hence the UNIQUE tag
                //in addition we add ON_CONFLICT_REPLACE to replace existing movie if we want to add
                //another one with the same id
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE)";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);

    }

    /**
     * Update database data when its version is upgraded
     * NOTE: we use this database to cache online data so we would refresh and create a new table
     * from on create if version was upgradede
     * @param db Database that is being upgraded
     * @param oldVersion database old version
     * @param newVersion database new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
