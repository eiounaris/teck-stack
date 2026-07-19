package com.eiou.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public final class JdbcApiDemo {
    private static final String URL = "jdbc:h2:mem:jdbc-demo;MODE=MySQL;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private JdbcApiDemo() {
    }

    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE jdbc_demo (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            amount DECIMAL(12, 2) NOT NULL
                        )
                        """);
            }

            long generatedId;
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO jdbc_demo (name, amount) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, "first");
                statement.setBigDecimal(2, new BigDecimal("10.00"));
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    keys.next();
                    generatedId = keys.getLong(1);
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id, name, amount FROM jdbc_demo WHERE id = ?"
            )) {
                statement.setLong(1, generatedId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        System.out.printf(
                                "query: id=%d, name=%s, amount=%s%n",
                                resultSet.getLong("id"),
                                resultSet.getString("name"),
                                resultSet.getBigDecimal("amount")
                        );
                    }
                }
            }

            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE jdbc_demo SET amount = amount + ? WHERE id = ?"
            )) {
                statement.setBigDecimal(1, new BigDecimal("5.00"));
                statement.setLong(2, generatedId);
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO jdbc_demo (name, amount) VALUES (?, ?)"
            )) {
                for (String name : List.of("second", "third")) {
                    statement.setString(1, name);
                    statement.setBigDecimal(2, new BigDecimal("20.00"));
                    statement.addBatch();
                }
                statement.executeBatch();
            }

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM jdbc_demo")) {
                resultSet.next();
                System.out.println("count: " + resultSet.getInt(1));
            }
        }
    }
}
