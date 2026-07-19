package com.eiou.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class MyBatisApiDemo {
    private MyBatisApiDemo() {
    }

    public static void main(String[] args) throws IOException {
        try (Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

            try (SqlSession session = sqlSessionFactory.openSession()) {
                session.update("mybatisDemo.createTable");

                session.insert("mybatisDemo.insertRow", Map.of(
                        "name", "first",
                        "amount", new BigDecimal("10.00")
                ));

                session.update("mybatisDemo.updateAmount", Map.of(
                        "name", "first",
                        "delta", new BigDecimal("5.00")
                ));

                List<Map<String, Object>> rows = session.selectList("mybatisDemo.selectRows");
                rows.forEach(row -> System.out.printf(
                        "row: id=%s, name=%s, amount=%s%n",
                        row.get("id"),
                        row.get("name"),
                        row.get("amount")
                ));

                session.commit();
            }
        }
    }
}
