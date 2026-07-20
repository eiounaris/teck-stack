# Spring JDBC 与事务

本模块演示 Spring Framework 的 `JdbcTemplate`、声明式事务和编程式事务，不包含 Spring MVC 和 Spring Boot。

示例默认使用 H2 内存数据库，因此不需要本地安装 MySQL 或其他数据库服务。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-jdbc-tx package
```

## 运行示例

```bash
mvn -pl java-spring-jdbc-tx org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.spring.jdbctx.SpringJdbcTxApiDemo
```

## 依赖

- `org.springframework:spring-context`：Spring 容器
- `org.springframework:spring-jdbc`：`JdbcTemplate` 和数据访问异常转换
- `org.springframework:spring-tx`：事务抽象与事务管理器
- `com.h2database:h2`：内存数据库驱动

## DataSource

```java
@Bean
DataSource dataSource() {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:spring-jdbc-tx;MODE=MySQL;DB_CLOSE_DELAY=-1");
    dataSource.setUser("sa");
    dataSource.setPassword("");
    return dataSource;
}
```

## JdbcTemplate

```java
jdbcTemplate.update(
        "INSERT INTO ledger_entry (order_no, entry_type, amount) VALUES (?, ?, ?)",
        orderNo,
        "DEBIT",
        amount
);
```

## 声明式事务

```java
@Transactional
public void recordPayment(String orderNo, BigDecimal amount) {
    // multiple SQL statements in one transaction
}
```

## 编程式事务

```java
transactionTemplate.executeWithoutResult(status -> {
    // explicit transaction boundary
});
```

## 事务失效重点

- `@Transactional` 基于 Spring AOP 代理。
- 同类内部调用事务方法不会经过代理。
- 默认只对 `RuntimeException` 和 `Error` 回滚。
- 被 `catch` 吞掉的异常不会触发事务回滚。

## 示例文件

查看 `src/main/java/com/eiou/spring/jdbctx`。
