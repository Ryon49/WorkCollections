package com.wdong.model.wrapper;

import java.util.HashMap;
import java.util.HashSet;

public class XmlParserWrapper extends ResponseWrapper {
    private HashSet<String> invalidMovies;
    private HashSet<String> invalidActors;
    private HashMap<String, HashSet<String>> invalidCasts;

    public XmlParserWrapper(HashSet<String> invalidMovies, HashSet<String> invalidActors, HashMap<String, HashSet<String>> invalidCasts) {
        this.invalidMovies = invalidMovies;
        this.invalidActors = invalidActors;
        this.invalidCasts = invalidCasts;
    }

    public HashSet<String> getInvalidMovies() {
        return invalidMovies;
    }

    public void setInvalidMovies(HashSet<String> invalidMovies) {
        this.invalidMovies = invalidMovies;
    }

    public HashSet<String> getInvalidActors() {
        return invalidActors;
    }

    public void setInvalidActors(HashSet<String> invalidActors) {
        this.invalidActors = invalidActors;
    }

    public HashMap<String, HashSet<String>> getInvalidCasts() {
        return invalidCasts;
    }

    public void setInvalidCasts(HashMap<String, HashSet<String>> invalidCasts) {
        this.invalidCasts = invalidCasts;
    }
}
