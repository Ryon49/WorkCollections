package com.wdong.cache;

import java.util.ArrayList;
import java.util.List;

public class StarMovieCache {

    private static String starName;

    private static List<String> ids;

    public static boolean hit(String name) {
        if (starName == null) {
            return false;
        }
        return starName.equals(name);
    }

    public static void cache(String name, List<String> idList) {
        starName = name;
        ids = idList;
    }

    public static List<String> getCache() {
        // maybe add validation logic
        return new ArrayList<>(ids);
    }
}
