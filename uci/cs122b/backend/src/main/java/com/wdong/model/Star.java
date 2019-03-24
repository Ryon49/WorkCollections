package com.wdong.model;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;


@Entity(name = "stars")
@Table(name = "stars")
public class Star {

    @Id
    private String id;
    private String name;

    @Column(name = "birthYear")
    private Integer birthYear;

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "stars")
    @JsonIgnoreProperties("stars")
    private List<Movie> movies;

    // region getter and setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        if (birthYear == null) {
            return 0;
        }
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    // endregion
}
