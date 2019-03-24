package com.wdong.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity(name = "genres")
public class Genre {

    // region properties
    @Id
    private int id;

    private String name;
    // endregion

    // region field many-to-many
    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "genres")
    @JsonIgnoreProperties("genres")
    private List<Movie> movies;
    // endregion

    // region getter and setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    // endregion
}
