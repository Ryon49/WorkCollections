package com.wdong.xml;

import com.wdong.config.GenreGenerator;
import com.wdong.model.Movie;
import com.wdong.model.simple.SimpleMovie;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MovieHandler extends DefaultHandler {

    private String value;

    private HashMap<String, SimpleMovie> movies = new HashMap<>();
    private HashSet<String> ids = new HashSet<>();
    private HashSet<String> invalids = new HashSet<>();

    private boolean allowAdd;
    private boolean setDirector = false;
    private SimpleMovie movie;

    public HashMap<String, SimpleMovie> getMovies() {
        return movies;
    }

    public HashSet<String> getInvalids() {
        return invalids;
    }

    @Override
    public void endElement(String s, String s1, String element) {
        switch (element) {
            case "film":
                if (allowAdd) {
                    if (movie.getDirector() == null) {
                        movie.setDirector("No director found");
                    }

                    movies.put(movie.getId(), movie);
                }
                break;
            case "fid":
                if (ids.contains(value)) {
                    invalids.add(value);
                    movies.remove(value);
                    allowAdd = false;
                } else {
                    ids.add(value);
                    movie = new SimpleMovie();
                    movie.setId(value);
                    allowAdd = true;
                    setDirector = true;
                }
                break;
            case "t":
                if (allowAdd) {
                    movie.setTitle(value);
                }
                break;
            case "year":
                if (allowAdd) {
                    movie.setYear(value);
                }
                break;
            case "dir":
                if (allowAdd && setDirector) {
                    movie.setDirector(value);
                    setDirector = false;
                }
                break;
            case "dirnote":
                if (allowAdd && setDirector) {
                    if (value.equals("no director named") || value.startsWith("no director listed")) {
                        movie.setDirector("No director");
                        setDirector = false;
                    } else if (value.startsWith("listed as")) {
                        movie.setDirector(value.substring(10));
                        setDirector = false;
                    }
                }
                break;
            case "dirn":
                if (allowAdd && setDirector && !value.equals("Unknown2")) {
                    movie.setDirector(value);
                    setDirector = false;
                }
                break;
            case "cat":
                if (allowAdd) {
                    movie.addGenre(GenreGenerator.get(value));
                }
                break;
        }
    }

    @Override
    public void characters(char[] ac, int i, int j) {
        value = new String(ac, i, j);
    }
}
