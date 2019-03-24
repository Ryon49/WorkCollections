package com.wdong.JpaTest;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JdbcTest {

    public void example() {
        DataSource dataSource = jdbcTemplate.getDataSource();
        try {
            Connection conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
}
