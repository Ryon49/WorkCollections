package com.wdong.model.wrapper;

import com.wdong.model.Movie;

import java.util.Iterator;

public class MoviesWrapper extends ResponseWrapper {

    // region properties
    private Iterator<Movie> movies;

    private int pageNum;

    private int totalPages;

    private int maxRecords;

    private int sortBy;

    private String searchDescription;
    // endregion

    // region constructor
    public MoviesWrapper(Iterator<Movie> movies, int pageNum, int totalPages, int maxRecords, int sortBy) {
        this.movies = movies;
        this.pageNum = pageNum;
        this.totalPages = totalPages;
        this.maxRecords = maxRecords;
        this.sortBy = sortBy;
    }
    // endregion

    // region getter and setter

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getSortBy() {
        return sortBy;
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    public Iterator<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Iterator<Movie> movies) {
        this.movies = movies;
    }

    public String getSearchDescription() {
        return searchDescription;
    }

    public void setSearchDescription(String searchDescription) {
        this.searchDescription = searchDescription;
    }

    // endregion
}
