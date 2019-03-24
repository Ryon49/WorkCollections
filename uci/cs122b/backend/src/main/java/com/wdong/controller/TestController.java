package com.wdong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "api/test")
public class TestController {

    // region master datasource
    @RequestMapping("master")
    public @ResponseBody
    ArrayList<String> master() {
        ArrayList<String> ret = new ArrayList<>();
        try {
            Connection connection = master.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select id, title from movies limit 5");
            while (rs.next()) {
                ret.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Autowired
    @Qualifier("master_datasource")
    private DataSource master;
    // endregion

    // region JdbcTemplate
    @RequestMapping("example")
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
    // endregion
}
