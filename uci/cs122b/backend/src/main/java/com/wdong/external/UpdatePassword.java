package com.wdong.external;

import com.wdong.model.Employee;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;

public class UpdatePassword {
    private static void customer(Connection conn) throws Exception {
        Statement statement = conn.createStatement();

        String alterQuery = "ALTER TABLE employees MODIFY COLUMN password VARCHAR(128)";
        int alterResult = statement.executeUpdate(alterQuery);
        System.out.println("altering customers table schema completed, " + alterResult + " rows affected");

        String query = "SELECT email, password from employees";
        ResultSet rs = statement.executeQuery(query);

        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

        int count = 0;
        System.out.println("encrypting employees password");
        PreparedStatement stmt = conn.prepareStatement("UPDATE employees SET password=? WHERE email=?;");
        while (rs.next()) {
            String encryptedPassword = passwordEncryptor.encryptPassword(rs.getString("password"));

            stmt.setString(1, encryptedPassword);
            stmt.setString(2, rs.getString("email"));
            stmt.addBatch();

            count += 1;
        }

        // execute the update queries to update the password
        System.out.println("updating password (this might take a while)");
        stmt.executeBatch();

        System.out.println("updating password completed, " + count + " rows affected");
        System.out.println("finished");
    }

    private static void employee(Connection conn) throws Exception {
        Statement statement = conn.createStatement();

        String alterQuery = "ALTER TABLE customers MODIFY COLUMN password VARCHAR(128)";
        int alterResult = statement.executeUpdate(alterQuery);
        System.out.println("altering customers table schema completed, " + alterResult + " rows affected");

        String query = "SELECT id, password from customers";

        ResultSet rs = statement.executeQuery(query);

        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

        PreparedStatement stmt = conn.prepareStatement("UPDATE customers SET password=? WHERE id=?;");
        int count = 0;

        System.out.println("encrypting password");
        while (rs.next()) {
            String encryptedPassword = passwordEncryptor.encryptPassword(rs.getString("password"));

            stmt.setString(1, encryptedPassword);
            stmt.setString(2, rs.getString("id"));
            stmt.addBatch();
            count += 1;
        }

        // execute the update queries to update the password
        System.out.println("updating customers password (this might take a while)");
        stmt.executeBatch();
        System.out.println("updating password completed, " + count + " rows affected");
        System.out.println("finished");
    }

    public static void changePassword(JdbcTemplate jdbcTemplate) throws Exception {
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        conn.setAutoCommit(false);
        employee(conn);
        customer(conn);
        conn.commit();
        conn.setAutoCommit(true);
    }
}
