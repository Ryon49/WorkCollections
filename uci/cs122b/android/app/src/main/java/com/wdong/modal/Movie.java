package com.wdong.modal;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class Movie implements Parcelable {
    private String id;
    private String title;
    private String director;
    private int year;
    private List<String> genres;
    private List<String> stars;


    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

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

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getGenres() {
        return this.genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getStars() {
        return this.stars;
    }

    public void setStars(List<String> stars) {
        this.stars = stars;
    }

    public Movie() { }

    private Movie(Parcel in) {
        this.title = in.readString();
        this.director = in.readString();
        this.year = in.readInt();
        this.genres = new ArrayList<>();
        this.stars = new ArrayList<>();
        in.readStringList(this.genres);
        in.readStringList(this.stars);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.director);
        dest.writeInt(this.year);
        dest.writeStringList(this.genres);
        dest.writeStringList(this.stars);
    }
}
