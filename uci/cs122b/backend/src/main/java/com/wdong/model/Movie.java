package com.wdong.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity(name = "movies")
@Table(name = "movies")
public class Movie {

    // region field
    @Id
    private String id;

    private String title;

    private int year;

    private String director;

    // endregion

    // region constructor
    public Movie() { }

    // endregion

    // region one-to-one
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Rating rating;
    // endregion

    // region many-to-many
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "genres_in_movies",
            joinColumns = @JoinColumn(name = "movieId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "genreId", referencedColumnName = "id"))
    @JsonIgnoreProperties(value = {"movies"})
    private List<Genre> genres;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "stars_in_movies",
            joinColumns = @JoinColumn(name = "movieId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "starId", referencedColumnName = "id"))
    @JsonIgnoreProperties(value = {"movies"})
    private List<Star> stars;

    // endregion

    // region setter and getter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public List<Star> getStars() {
        return stars;
    }

    public void setStars(List<Star> stars) {
        this.stars = stars;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
    // endregion
}
