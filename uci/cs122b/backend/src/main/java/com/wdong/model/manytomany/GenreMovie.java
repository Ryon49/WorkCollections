package com.wdong.model.manytomany;

import javax.persistence.*;

@Entity
@Table(name = "genres_in_movies")
public class GenreMovie {

    @EmbeddedId
    private GenreMovieId id;

    public GenreMovieId getId() {
        return id;
    }

    public void setId(GenreMovieId id) {
        this.id = id;
    }
}
