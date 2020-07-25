package com.example.popularmovies.Data;

/**
 * class to get extra movie data from network that is not saved in our DB (Content Provider)
 */
public class ExtraMovieData {
    private String[] video_ids;
    private String[][] reviews;

    public ExtraMovieData(String[] video_ids, String[][] reviews) {
        this.video_ids = video_ids;
        this.reviews = reviews;
    }

    public String[] getVideo_ids() {
        return video_ids;
    }

    public String[][] getReviews() {
        return reviews;
    }
}
