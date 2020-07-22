package com.example.popularmovies.Data;
/*
 * Class for representing movie as an object in our code
 */
public class Movie {

    private int id;
    private String title;
    private String posterPath;
    private String backgdropPath;
    private int[] clip_keys;
    private String synopsis;
    private String[] reviews;
    private double user_rating;
    private double popularity;
    private String releaseDate;
    private boolean isFav;

    public Movie(int id, String title, String posterPath, String backgdropPath, int[] clip_keys, String synopsis, String[] reviews, double user_rating, double popularity, String releaseDate, boolean isFav) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.backgdropPath = backgdropPath;
        this.clip_keys = clip_keys;
        this.synopsis = synopsis;
        this.reviews = reviews;
        this.user_rating = user_rating;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.isFav = isFav;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackgdropPath() {
        return backgdropPath;
    }

    public void setBackgdropPath(String backgdropPath) {
        this.backgdropPath = backgdropPath;
    }

    public int[] getTrailerPaths() {
        return clip_keys;
    }

    public void setTrailerPaths(int[] clip_ids) {
        this.clip_keys = clip_ids;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String[] getReviews() {
        return reviews;
    }

    public void setReviews(String[] reviews) {
        this.reviews = reviews;
    }

    public double getUser_rating() {
        return user_rating;
    }

    public void setUser_rating(Double user_rating) {
        this.user_rating = user_rating;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }
}
