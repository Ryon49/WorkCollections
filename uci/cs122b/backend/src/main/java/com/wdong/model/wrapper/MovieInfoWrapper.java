package com.wdong.model.wrapper;

import com.wdong.model.Movie;
import com.wdong.model.SimpleGenre;

import java.util.List;

public class MovieInfoWrapper extends ResponseWrapper {
    // region properties
    private List<String> letters;

    private Iterable<SimpleGenre> genreList;

    private List<Movie> top20Movies;
    // endregion

    // region constructor

    public MovieInfoWrapper(List<String> letters, Iterable<SimpleGenre> genreList,
                            List<Movie> top20Movies) {
        this.letters = letters;
        this.genreList = genreList;
        this.top20Movies = top20Movies;
    }

    // endregion

    // region getter and setter

    public List<String> getLetters() {
        return letters;
    }

    public void setLetters(List<String> letters) {
        this.letters = letters;
    }

    public Iterable<SimpleGenre> getGenreList() {
        return genreList;
    }

    public void setGenreList(Iterable<SimpleGenre> genreList) {
        this.genreList = genreList;
    }

    public List<Movie> getTop20Movies() {
        return top20Movies;
    }

    public void setTop20Movies(List<Movie> top20Movies) {
        this.top20Movies = top20Movies;
    }


    // endregion


}
