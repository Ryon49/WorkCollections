package com.wdong.config;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class GenreGenerator {

    public static boolean initialized = false;
    private static HashMap<String, Integer> genreMapping;
    private static HashMap<String, Integer> newGenreMapping;

    private static int maxId = 0;

    public static int get(String genreName) {
        if (!initialized) {
            throw new RuntimeException("Important configuration not yet initialized");
        }
        if (genreMapping.containsKey(genreName)) {
            return genreMapping.get(genreName);
        } else if (newGenreMapping != null && newGenreMapping.containsKey(genreName))  {
            return newGenreMapping.get(genreName);
        } else {
            return newId(genreName);
        }
    }

    public static boolean hasNewGenre() {
        return newGenreMapping != null && newGenreMapping.size() > 0;
    }

    public static HashMap<String, Integer> getNewGenreMapping() {
        return newGenreMapping;
    }

    public static void init(DataSource ds) {
        genreMapping = new HashMap<>();
        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select id, name from genres");
            while (rs.next()) {
                int genreId = rs.getInt("id");
                String genreName = rs.getString("name");
                genreMapping.put(genreName, genreId);
                maxId = Math.max(maxId, genreId);
            }
            initialized = true;
            stmt.close();
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void merge() {
        if (!initialized) {
            throw new RuntimeException("Important configuration not yet initialized");
        }
        if (newGenreMapping == null) {
            return;
        }
        genreMapping.putAll(newGenreMapping);
    }

    private static int newId(String genreName) {
        if (!initialized) {
            throw new RuntimeException("Important configuration not yet initialized");
        }
        if (newGenreMapping == null) {
            newGenreMapping = new HashMap<>();
        }
        maxId = maxId + 1;
        newGenreMapping.put(genreName, maxId);
        return maxId;
    }

}
