package com.example.popularmovies.Data;
/*
 * Class for representing movie as an object in our code
 */
public class Movie {

    private int id;
    private String title;
    private String posterPath;
    private String backgdropPath;
    private String[] trailerPaths;
    private String synopsis;
    private String[] reviews;
    private double user_rating;
    private String releaseDate;
    private boolean isFav;

    /**
     * Create a new movie with a unique Id
     * sets all values to null (rating = 0, isFav = false)
     * @param id
     */
    public Movie(int id){
        this.id = id;
        this.title = null;
        this.posterPath = null;
        this.backgdropPath = null;
        this.trailerPaths = null;
        this.synopsis = null;
        this.reviews = null;
        this.user_rating = 0;
        this.releaseDate = null;
        this.isFav = false;
    }

    public Movie(int id, String title, String posterPath, String backgdropPath, String[] trailerPaths, String synopsis, String[] reviews, double user_rating, String releaseDate, boolean isFav) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.backgdropPath = backgdropPath;
        this.trailerPaths = trailerPaths;
        this.synopsis = synopsis;
        this.reviews = reviews;
        this.user_rating = user_rating;
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

    public String[] getTrailerPaths() {
        return trailerPaths;
    }

    public void setTrailerPaths(String[] trailerPaths) {
        this.trailerPaths = trailerPaths;
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
