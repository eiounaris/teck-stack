package com.eiou.mybatis.practice;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public final class MyBatisPracticeDemo {
    private MyBatisPracticeDemo() {
    }

    public static void main(String[] args) throws IOException {
        Properties properties = loadProperties();

        try (HikariDataSource dataSource = createDataSource(properties)) {
            SqlSessionFactory sqlSessionFactory = createSqlSessionFactory(dataSource);

            Long aliceId = insertUsers(sqlSessionFactory);
            rollbackStatusChange(sqlSessionFactory, aliceId);
            printActiveUsers(sqlSessionFactory, aliceId);
        }
    }

    private static Long insertUsers(SqlSessionFactory sqlSessionFactory) {
        try (SqlSession session = sqlSessionFactory.openSession(false)) {
            try {
                UserMapper mapper = session.getMapper(UserMapper.class);
                mapper.createTable();

                DemoUser alice = new DemoUser(
                        "alice",
                        "ACTIVE",
                        new BigDecimal("100.00"),
                        LocalDateTime.now()
                );
                mapper.insertUser(alice);

                mapper.insertUsers(List.of(
                        new DemoUser("bob", "ACTIVE", new BigDecimal("80.00"), LocalDateTime.now()),
                        new DemoUser("cindy", "LOCKED", new BigDecimal("50.00"), LocalDateTime.now())
                ));

                session.commit();
                System.out.println("insertedId: " + alice.getId());
                return alice.getId();
            } catch (RuntimeException exception) {
                session.rollback();
                throw exception;
            }
        }
    }

    private static void rollbackStatusChange(SqlSessionFactory sqlSessionFactory, long userId) {
        try (SqlSession session = sqlSessionFactory.openSession(false)) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.updateStatus(userId, "LOCKED");
            session.rollback();
        }
    }

    private static void printActiveUsers(SqlSessionFactory sqlSessionFactory, long userId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            DemoUser alice = mapper.findById(userId);
            List<DemoUser> activeUsers = mapper.findPageByStatus("ACTIVE", 10, 0);

            System.out.println("afterRollback: " + alice.getStatus());
            activeUsers.forEach(user -> System.out.println("pageRow: " + user));
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = Resources.getResourceAsStream("application.properties")) {
            properties.load(inputStream);
        }

        return properties;
    }

    private static HikariDataSource createDataSource(Properties properties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("db.jdbcUrl"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));
        config.setPoolName(properties.getProperty("hikari.poolName", "mybatis-practice-pool"));
        config.setMaximumPoolSize(intProperty(properties, "hikari.maximumPoolSize", 5));
        config.setMinimumIdle(intProperty(properties, "hikari.minimumIdle", 1));
        config.setConnectionTimeout(longProperty(properties, "hikari.connectionTimeout", 3000));
        config.setIdleTimeout(longProperty(properties, "hikari.idleTimeout", 600000));
        config.setMaxLifetime(longProperty(properties, "hikari.maxLifetime", 1800000));
        return new HikariDataSource(config);
    }

    private static SqlSessionFactory createSqlSessionFactory(HikariDataSource dataSource) throws IOException {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("h2", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);

        try (InputStream mapperXml = Resources.getResourceAsStream("mappers/UserMapper.xml")) {
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(
                    mapperXml,
                    configuration,
                    "mappers/UserMapper.xml",
                    configuration.getSqlFragments()
            );
            mapperParser.parse();
        }

        return new SqlSessionFactoryBuilder().build(configuration);
    }

    private static int intProperty(Properties properties, String key, int defaultValue) {
        return Integer.parseInt(properties.getProperty(key, Integer.toString(defaultValue)));
    }

    private static long longProperty(Properties properties, String key, long defaultValue) {
        return Long.parseLong(properties.getProperty(key, Long.toString(defaultValue)));
    }
}
