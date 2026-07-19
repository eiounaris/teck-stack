# HikariCP API 使用

本模块演示 HikariCP 的最小依赖、最小配置和最直接 API 调用。示例默认使用 H2 内存数据库，方便直接运行连接池示例。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-hikaricp package
```

## 运行示例

```bash
mvn -pl java-hikaricp org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.hikaricp.HikariCpApiDemo
```

## 依赖

- `com.zaxxer:HikariCP`：JDBC 连接池
- `com.h2database:h2`：内存数据库驱动，方便本地直接运行

## HikariConfig

```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:h2:mem:hikari-demo;MODE=MySQL;DB_CLOSE_DELAY=-1");
config.setUsername("sa");
config.setPassword("");
config.setPoolName("hikari-demo-pool");
config.setMaximumPoolSize(3);
config.setConnectionTimeout(3000);
```

## HikariDataSource

```java
try (HikariDataSource dataSource = new HikariDataSource(config)) {
    // use dataSource
}
```

## 获取连接

```java
try (Connection connection = dataSource.getConnection()) {
    // use JDBC API
}
```

## 执行 SQL

```java
try (Connection connection = dataSource.getConnection();
     Statement statement = connection.createStatement()) {
    statement.execute("""
            CREATE TABLE hikari_demo (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL
            )
            """);
}
```

## PreparedStatement

```java
try (Connection connection = dataSource.getConnection();
     PreparedStatement statement = connection.prepareStatement(
             "INSERT INTO hikari_demo (name) VALUES (?)"
     )) {
    statement.setString(1, "first");
    statement.executeUpdate();
}
```

## 读取配置

```java
String poolName = dataSource.getPoolName();
int maximumPoolSize = dataSource.getMaximumPoolSize();
long connectionTimeout = dataSource.getConnectionTimeout();
```

## 关闭连接池

```java
dataSource.close();
```

使用 `try-with-resources` 创建 `HikariDataSource` 时会自动关闭连接池。

## 日志说明

HikariCP 使用 SLF4J。本模块保持最小依赖，不额外引入日志实现；如需日志输出，可参考 `java-slf4j-logback` 模块。

## 示例文件

查看 `src/main/java/com/eiou/hikaricp/HikariCpApiDemo.java`。
