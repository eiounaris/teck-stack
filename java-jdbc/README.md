# JDBC API 使用

本模块演示 Java JDBC 的常用 API。示例默认使用 H2 内存数据库，因此不需要本地安装 MySQL 或其他数据库服务。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-jdbc package
```

## 运行示例

```bash
mvn -pl java-jdbc org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.jdbc.JdbcApiDemo
```

## 依赖

- `com.h2database:h2`：内存数据库驱动，方便本地直接运行 JDBC 示例

## 建立连接

```java
Connection connection = DriverManager.getConnection(
        "jdbc:h2:mem:jdbc-demo;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "sa",
        ""
);
```

## Statement

```java
try (Statement statement = connection.createStatement()) {
    statement.execute("""
            CREATE TABLE jdbc_demo (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                amount DECIMAL(12, 2) NOT NULL
            )
            """);
}
```

## PreparedStatement

```java
try (PreparedStatement statement = connection.prepareStatement(
        "INSERT INTO jdbc_demo (name, amount) VALUES (?, ?)",
        Statement.RETURN_GENERATED_KEYS
)) {
    statement.setString(1, "first");
    statement.setBigDecimal(2, new BigDecimal("10.00"));
    statement.executeUpdate();
}
```

## ResultSet

```java
try (PreparedStatement statement = connection.prepareStatement(
        "SELECT id, name, amount FROM jdbc_demo WHERE id = ?"
)) {
    statement.setLong(1, 1L);
    try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            BigDecimal amount = resultSet.getBigDecimal("amount");
        }
    }
}
```

## 事务

```java
connection.setAutoCommit(false);
try {
    // update records
    connection.commit();
} catch (SQLException exception) {
    connection.rollback();
    throw exception;
}
```

## 批量写入

```java
try (PreparedStatement statement = connection.prepareStatement(
        "INSERT INTO jdbc_demo (name, amount) VALUES (?, ?)"
)) {
    statement.setString(1, "second");
    statement.setBigDecimal(2, new BigDecimal("20.00"));
    statement.addBatch();
    statement.executeBatch();
}
```

## MySQL 迁移提示

JDBC API 基本保持一致。切换到 MySQL 时，通常只需要：

- 将 JDBC URL 改为 `jdbc:mysql://localhost:3306/<database>?useSSL=false&serverTimezone=Asia/Shanghai`
- 将 H2 依赖替换为 `com.mysql:mysql-connector-j`
- 使用 `docker/mysql` 目录中的 MySQL Compose 配置启动本地数据库

## 示例文件

查看 `src/main/java/com/eiou/jdbc/JdbcApiDemo.java`。
