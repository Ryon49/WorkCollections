package com.wdong.model.manytomany;


import com.wdong.model.Movie;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
public class GenreMovieId implements Serializable {
    private int genreId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movieId", referencedColumnName = "id")
    private Movie movie;

    // region getter and setter
    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

// endregion
}
