package com.wdong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

@Component
@ConfigurationProperties(prefix = "modal.id.prefix")
public class IdGenerator {
    public static boolean initialized;

    public enum type {Movie, Star, Sale}

    private static final HashMap<type, String> prefixes = new HashMap<>();

    private static final HashMap<type, Integer> ids = new HashMap<>();

    static {
        initialized = false;
        prefixes.put(type.Movie, "tt");
        prefixes.put(type.Star, "nm");
    }

    public static String getStringId(type type) {
        String prefix = prefixes.get(type);

        int id = ids.get(type);
        ids.put(type, id + 1);

        if (id == -1) {
            throw new RuntimeException("synchronized failed");
        }

        if (id < 1000000 && id >= 100000) {
            return String.format("%s0%d", prefix, id);
        } else if (id < 100000 && id >= 10000) {
            return String.format("%s00%d", prefix, id);
        } else if (id < 10000 && id >= 1000) {
            return String.format("%s000%d", prefix, id);
        } else if (id < 1000 && id >= 100) {
            return String.format("%s0000%d", prefix, id);
        } else if (id < 100 && id >= 10) {
            return String.format("%s00000%d", prefix, id);
        } else if (id < 10) {
            return String.format("%s000000%d", prefix, id);
        } else {
            return String.format("%s%d", prefix, id);
        }
    }

    public static int getIntId(type type) {
        int id = ids.get(type);
        ids.put(type, id + 1);
        return id;
    }

    public static void init(DataSource ds) {
        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select max(id) from movies where id like 'tt%'");
            if (rs.next()) {
                ids.put(type.Movie, Integer.parseInt(rs.getString(1).substring(2)) + 1);
            }

            rs = stmt.executeQuery("select max(id) from stars where id like 'nm%'");
            if (rs.next()) {
                ids.put(type.Star, Integer.parseInt(rs.getString(1).substring(2)) + 1);
            }

            rs = stmt.executeQuery("select max(id) from sales");
            if (rs.next()) {
                ids.put(type.Sale, rs.getInt(1));
            }
            initialized = true;
            stmt.close();
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
