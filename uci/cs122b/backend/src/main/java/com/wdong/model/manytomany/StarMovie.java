package com.wdong.model.manytomany;

import javax.persistence.*;

@Entity
@Table(name = "stars_in_movies")
public class StarMovie {
    @EmbeddedId
    private StarMovieId id;

    public StarMovieId getId() {
        return id;
    }

    public void setId(StarMovieId id) {
        this.id = id;
    }

    public interface Projection {
        String getIdMovieId();
    }
}
