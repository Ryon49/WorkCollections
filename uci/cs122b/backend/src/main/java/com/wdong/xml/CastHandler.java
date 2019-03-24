package com.wdong.xml;

import com.wdong.model.simple.SimpleMovie;
import com.wdong.model.simple.SimpleStar;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.HashSet;

public class CastHandler extends DefaultHandler {

    private String value;

    private HashMap<String, SimpleMovie> movies;
    private HashMap<String, SimpleStar> stars;

    private HashMap<String, HashSet<String>> invalids;

    private String movieId;
    private SimpleMovie movie;  // refer to one of SimpleMovie in this.movies

    public CastHandler(HashMap<String, SimpleMovie> movies, HashMap<String, SimpleStar> stars) {
        movieId = "";
        this.movies = movies;
        this.stars = stars;

        invalids = new HashMap<>();
    }

    public HashMap<String, HashSet<String>> getInvalids() {
        return invalids;
    }

    @Override
    public void endElement(String s, String s1, String element) {
        switch (element) {
            case "f":
                if (!movieId.equalsIgnoreCase(value)) {
                    movie = movies.get(value);
                    movieId = value;
                }
                break;
            case "a":
                if (stars.containsKey(value) && movie != null) {
                    movie.addStar(stars.get(value).getId());
                } else {
                    if (invalids.containsKey(movieId)) {
                        invalids.get(movieId).add(value);
                    } else {
                        HashSet<String> newInvalids = new HashSet<>();
                        newInvalids.add(value);
                        invalids.put(movieId, newInvalids);
                    }
                }
                break;
        }
    }

    @Override
    public void characters(char[] ac, int i, int j) {
        value = new String(ac, i, j);
    }
}
