package com.wdong.model.simple;

import java.util.HashSet;

public class SimpleMovie {

    private String id;

    private String title;

    private int year;

    private String director;

    private HashSet<Integer> genreIds;

    private HashSet<String> starIds;

    public void addGenre(int genreId) {
        if (genreIds == null) {
            genreIds = new HashSet<>();
        }
        genreIds.add(genreId);
    }

    public void addStar(String starId) {
        if (starIds == null) {
            starIds = new HashSet<>();
        }
        starIds.add(starId);
    }


    // region getter and setter

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

    public void setYear(String year) {
        try {
            setYear(Integer.parseInt(year));
        } catch (Exception e) {
            setYear(0);
        }
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public HashSet<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(HashSet<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public HashSet<String> getStarIds() {
        return starIds;
    }

    public void setStarIds(HashSet<String> starIds) {
        this.starIds = starIds;
    }

    // endregion
}
