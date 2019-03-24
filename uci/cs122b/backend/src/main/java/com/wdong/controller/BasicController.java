package com.wdong.controller;

import com.wdong.config.GenreGenerator;
import com.wdong.config.IdGenerator;
import com.wdong.external.UpdatePassword;
import com.wdong.model.Response;
import com.wdong.model.db.Table;
import com.wdong.model.db.TableColumn;
import com.wdong.model.simple.SimpleMovie;
import com.wdong.model.simple.SimpleStar;
import com.wdong.model.wrapper.MovieInfoWrapper;
import com.wdong.model.wrapper.SchemeWrapper;
import com.wdong.model.wrapper.XmlParserWrapper;
import com.wdong.repository.MoviesRepository;
import com.wdong.repository.simple.SimpleGenresRepository;
import com.wdong.xml.ActorHandler;
import com.wdong.xml.CastHandler;
import com.wdong.xml.MovieHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
public class BasicController {
    @EventListener(ApplicationReadyEvent.class)
    public void setup() {
        if (!GenreGenerator.initialized) {
            GenreGenerator.init(jdbcTemplate.getDataSource());
        }
        if (!IdGenerator.initialized) {
            IdGenerator.init(jdbcTemplate.getDataSource());
        }
    }

    // region API
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/basic/ds")
    public @ResponseBody String dataSourceType() {
        String code = "Code:\t\tString.format(\"DataSource Class: %s\", " +
                "jdbcTemplate.getDataSource());\n\n\n";

        String output = String.format("Output:\t\tDataSource Class: %s",
                jdbcTemplate.getDataSource());

        return code + output;
    }

    @GetMapping("/")
    public @ResponseBody String index() {
        return "Success.\n\nThere is nothing here";
    }

    @GetMapping("/api/basic/info")
    public @ResponseBody Response info() {
        Pattern p = Pattern.compile("[^a-zA-Z0-9]");

        List<String> letters = moviesRepository.getTitleInfo().stream()
                .filter(s -> !p.matcher(s.substring(0, 1)).find()).collect(Collectors.toList());
        MovieInfoWrapper wrapper = new MovieInfoWrapper(
                letters,
                simpleGenresRepository.findAll(),
                moviesRepository.findFirst20ByOrderByRating_RatingDesc());
        return Response.ok(wrapper);
    }

    @GetMapping("/api/basic/scheme")
    public @ResponseBody Response scheme() {
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables where table_schema='moviedb';");
            ArrayList<Table> tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(new Table(rs.getString(1)));
            }

            for (Table table : tables) {
                String sql = "show columns in " + table.getTableName();
                rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    table.addColumn(new TableColumn(rs.getString(1), rs.getString(2), !rs.getString(4).equals("")));
                }
            }
            stmt.close();
            rs.close();
            conn.close();
            return Response.ok(new SchemeWrapper(tables));
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Similar some error happens");
        }
    }

    @GetMapping("/api/basic/encrypt")
    public @ResponseBody String encrypt() {
        try {
            long startTime = System.nanoTime();
            UpdatePassword.changePassword(jdbcTemplate);
            double seconds = (double)(System.nanoTime() - startTime) / 1_000_000_000.0;
            return "success \t time: " + seconds;
        } catch (Exception e) {
            return "Failed";
        }
    }

    @GetMapping("/api/basic/xml")
    public @ResponseBody Response xml() {
        long startTime = System.nanoTime();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            Connection conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);

            // parse stars
            File file = ResourceUtils.getFile("classpath:xml/actors63.xml");
            ActorHandler actorHandler = new ActorHandler();
            saxParser.parse(file, actorHandler);
            HashMap<String, SimpleStar> stars = actorHandler.getStars();

            PreparedStatement starStmt = conn.prepareStatement("insert into stars(id, name, birthYear) values (?,?,?)");
            for (SimpleStar movie : stars.values()) {
                starStmt.setString(1, movie.getId());
                starStmt.setString(2, movie.getName());
                starStmt.setInt(3, movie.getBirthYear());
                starStmt.addBatch();
            }

            // parse movies
            file = ResourceUtils.getFile("classpath:xml/mains243.xml");
            MovieHandler movieHandler = new MovieHandler();
            saxParser.parse(file, movieHandler);

            // prepare to insert new genres
            PreparedStatement newGenreStmt = null;
            if (GenreGenerator.hasNewGenre()) {
                newGenreStmt = conn.prepareStatement("insert into genres(id, name) values (?, ?)");
                for (Map.Entry<String, Integer> entry : GenreGenerator.getNewGenreMapping().entrySet()) {
                    newGenreStmt.setInt(1, entry.getValue());
                    newGenreStmt.setString(2, entry.getKey());
                    newGenreStmt.addBatch();
                }
            }
            HashMap<String, SimpleMovie> movies = movieHandler.getMovies();

            // parse casts
            file = ResourceUtils.getFile("classpath:xml/casts124.xml");
            CastHandler castHandler = new CastHandler(movies, stars);
            saxParser.parse(file, castHandler);

            // prepare to insert new movies and genres_in_movies and stars_in_movies
            PreparedStatement movieStmt = conn.prepareStatement("insert into movies(id, title, year, director) values (?,?,?,?)");
            PreparedStatement movieGenreStmt = conn.prepareStatement("insert into genres_in_movies(genreId, movieId) values (?, ?)");
            PreparedStatement movieStarStmt = conn.prepareStatement("insert into stars_in_movies(starId, movieId) values (?, ?)");

            for (SimpleMovie movie : movies.values()) {
                String id = movie.getId();
                movieStmt.setString(1, id);
                movieStmt.setString(2, movie.getTitle());
                movieStmt.setInt(3,movie.getYear());
                movieStmt.setString(4,movie.getDirector());
                movieStmt.addBatch();

                if (movie.getStarIds() != null) {
                    for (String starId : movie.getStarIds()) {
                        movieStarStmt.setString(1, starId);
                        movieStarStmt.setString(2, id);
                        movieStarStmt.addBatch();
                    }
                }

                if (movie.getGenreIds() != null) {
                    for (Integer genreId : movie.getGenreIds()) {
                        movieGenreStmt.setInt(1, genreId);
                        movieGenreStmt.setString(2, id);
                        movieGenreStmt.addBatch();
                    }
                }
            }

            GenreGenerator.merge();
            // execute batch
            if (newGenreStmt != null) {
                newGenreStmt.executeBatch();
            }
            starStmt.executeBatch();
            movieStmt.executeBatch();
            movieStarStmt.executeBatch();
            movieGenreStmt.executeBatch();

            conn.commit();
            if (newGenreStmt != null) {
                newGenreStmt.close();
            }
            starStmt.close();
            movieStmt.close();
            movieStarStmt.close();
            movieGenreStmt.close();
            conn.setAutoCommit(true);

            double seconds = (double)(System.nanoTime() - startTime) / 1_000_000_000.0;
            return Response.ok("Success \t time: " + seconds, new XmlParserWrapper(movieHandler.getInvalids(),
                    actorHandler.getInvalids(), castHandler.getInvalids()));
        } catch (Exception e) {
            return Response.error("Something goes wrong");
        }
    }

    // endregion

    // region Repository
    private final MoviesRepository moviesRepository;

    private final SimpleGenresRepository simpleGenresRepository;

    // endregion

    // region Wire Repository

    @Autowired
    public BasicController(MoviesRepository moviesRepository, SimpleGenresRepository simpleGenresRepository) {
        this.moviesRepository = moviesRepository;
        this.simpleGenresRepository = simpleGenresRepository;
    }
    // endregion
}
