package com.eiou.hikaricp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class HikariCpApiDemo {
    private static final String JDBC_URL = "jdbc:h2:mem:hikari-demo;MODE=MySQL;DB_CLOSE_DELAY=-1";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private HikariCpApiDemo() {
    }

    public static void main(String[] args) throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setPoolName("hikari-demo-pool");
        config.setMaximumPoolSize(3);
        config.setConnectionTimeout(3000);

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute("""
                        CREATE TABLE hikari_demo (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            amount DECIMAL(12, 2) NOT NULL
                        )
                        """);
            }

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO hikari_demo (name, amount) VALUES (?, ?)"
                 )) {
                statement.setString(1, "first");
                statement.setBigDecimal(2, new BigDecimal("10.00"));
                statement.executeUpdate();
            }

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT id, name, amount FROM hikari_demo WHERE name = ?"
                 )) {
                statement.setString(1, "first");

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

            System.out.println("poolName: " + dataSource.getPoolName());
            System.out.println("maximumPoolSize: " + dataSource.getMaximumPoolSize());
            System.out.println("connectionTimeout: " + dataSource.getConnectionTimeout());
        }
    }
}
