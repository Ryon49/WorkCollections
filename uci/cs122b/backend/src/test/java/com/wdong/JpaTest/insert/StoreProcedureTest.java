package com.wdong.JpaTest.insert;

import com.wdong.config.IdGenerator;
import com.wdong.repository.MoviesRepository;
import com.wdong.repository.StarsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StoreProcedureTest {

//    @Test
//    public void test() {
//        String movieId = IdGenerator.getStringId(IdGenerator.type.Movie);
//        String starId = IdGenerator.getStringId(IdGenerator.type.Star);
//
//        System.out.println(movieId);
//        System.out.println(starId);
//        try {
//            Connection conn = jdbcTemplate.getDataSource().getConnection();
//            CallableStatement stmt = conn.prepareCall("call add_movie(?,?,?,?,?,?,?)");
//            conn.prepareCall("call add_movie(?,?,?,?,?,?,?)");
//            stmt.setString("_movieId", movieId);
//            stmt.setString("_title", "My test movie v1.6");
//            stmt.setInt("_year", 2019);
//            stmt.setString("_director", "Ryon49");
//            stmt.setString("_genre", "ANIMATION");
//            stmt.setString("_starId", starId);
//            stmt.setString("_starName", "Ryon49 V3");
//
//            boolean success = stmt.execute();
//            System.out.println(success);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    private MoviesRepository moviesRepository;
//
//    @Autowired
//    private StarsRepository starsRepository;
}
