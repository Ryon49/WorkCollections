package com.wdong.xml;

import com.wdong.config.GenreGenerator;
import com.wdong.config.IdGenerator;
import com.wdong.model.simple.SimpleMovie;
import com.wdong.model.simple.SimpleStar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class Parser {
//    @Test
//    public void parse() {
//        if (!GenreGenerator.initialized) {
//            GenreGenerator.init(jdbcTemplate);
//        }
//        if (!IdGenerator.initialized) {
//            IdGenerator.init(jdbcTemplate);
//        }
//        System.out.println(GenreGenerator.get("Fantasy"));
//        try {
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            SAXParser saxParser = factory.newSAXParser();
//
//            Connection conn = jdbcTemplate.getDataSource().getConnection();
//            conn.setAutoCommit(false);
//
//            // parse stars
//            File file = ResourceUtils.getFile("classpath:xml/actors63.xml");
//            ActorHandler actorHandler = new ActorHandler();
//            saxParser.parse(file, actorHandler);
//            HashMap<String, SimpleStar> stars = actorHandler.getStars();
//
//            PreparedStatement starStmt = conn.prepareStatement("insert into stars(id, name, birthYear) values (?,?,?)");
//            for (SimpleStar movie : stars.values()) {
//                starStmt.setString(1, movie.getId());
//                starStmt.setString(2, movie.getName());
//                starStmt.setInt(3, movie.getBirthYear());
//                starStmt.addBatch();
//            }
//
//            // parse movies
//            file = ResourceUtils.getFile("classpath:xml/mains243.xml");
//            MovieHandler movieHandler = new MovieHandler();
//            saxParser.parse(file, movieHandler);
//
//            // prepare to insert new genres
//            PreparedStatement newGenreStmt = null;
//            if (GenreGenerator.hasNewGenre()) {
//                newGenreStmt = conn.prepareStatement("insert into genres(id, name) values (?, ?)");
//                for (Map.Entry<String, Integer> entry : GenreGenerator.getNewGenreMapping().entrySet()) {
//                    newGenreStmt.setInt(1, entry.getValue());
//                    newGenreStmt.setString(2, entry.getKey());
//                    newGenreStmt.addBatch();
//                }
//            }
//            HashMap<String, SimpleMovie> movies = movieHandler.getInvalidMovies();
//
//            // parse casts
//            file = ResourceUtils.getFile("classpath:xml/casts124.xml");
//            CastHandler castHandler = new CastHandler(movies, stars);
//            saxParser.parse(file, castHandler);
//
//            // prepare to insert new movies and genres_in_movies and stars_in_movies
//            PreparedStatement movieStmt = conn.prepareStatement("insert into movies(id, title, year, director) values (?,?,?,?)");
//            PreparedStatement movieGenreStmt = conn.prepareStatement("insert into genres_in_movies(genreId, movieId) values (?, ?)");
//            PreparedStatement movieStarStmt = conn.prepareStatement("insert into stars_in_movies(starId, movieId) values (?, ?)");
//
//            for (SimpleMovie movie : movies.values()) {
//                String id = movie.getId();
//                movieStmt.setString(1, id);
//                movieStmt.setString(2, movie.getTitle());
//                movieStmt.setInt(3,movie.getYear());
//                movieStmt.setString(4,movie.getDirector());
//                movieStmt.addBatch();
//
//                if (movie.getStarIds() != null) {
//                    for (String starId : movie.getStarIds()) {
//                        movieStarStmt.setString(1, starId);
//                        movieStarStmt.setString(2, id);
//                        movieStarStmt.addBatch();
//                    }
//                }
//
//                if (movie.getGenreIds() != null) {
//                    for (Integer genreId : movie.getGenreIds()) {
//                        movieGenreStmt.setInt(1, genreId);
//                        movieGenreStmt.setString(2, id);
//                        movieGenreStmt.addBatch();
//                    }
//                }
//            }
//
//            // execute batch
//            if (newGenreStmt != null) {
//                newGenreStmt.executeBatch();
//            }
//            starStmt.executeBatch();
//            movieStmt.executeBatch();
//            movieStarStmt.executeBatch();
//            movieGenreStmt.executeBatch();
//
//            conn.commit();
//            conn.setAutoCommit(true);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
}
