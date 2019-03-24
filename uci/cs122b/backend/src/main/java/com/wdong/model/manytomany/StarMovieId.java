package com.wdong.model.manytomany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wdong.model.Star;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class StarMovieId implements Serializable {
    @Column(name = "movieId")
    private String movieId;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "starId", referencedColumnName = "id")
    private Star star;

    // region getter and setter
    public Star getStar() {
        return star;
    }

    public void setStar(Star star) {
        this.star = star;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    // endregion
}
