# MyBatis 实战示例

本模块演示一个更接近项目实战的 MyBatis 写法。和 `java-mybatis` 的 API 速查不同，本模块包含外部配置、HikariCP 连接池、Mapper 接口、XML mapper、事务边界、分页查询和批量插入。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-mybatis-practice package
```

## 运行示例

```bash
mvn -pl java-mybatis-practice org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.mybatis.practice.MyBatisPracticeDemo
```

## 依赖

- `org.mybatis:mybatis`：MyBatis 核心 API
- `com.zaxxer:HikariCP`：数据库连接池
- `com.h2database:h2`：内存数据库驱动，方便本地运行
- `ch.qos.logback:logback-classic`：SLF4J 日志实现

## 实战点

- 外部化数据库和连接池配置：`src/main/resources/application.properties`
- 编程式组装 `HikariDataSource` 和 `SqlSessionFactory`
- Mapper 接口和 XML SQL 分离
- 显式事务：`openSession(false)`、`commit()`、`rollback()`
- `useGeneratedKeys` 获取自增主键
- `resultMap` 映射下划线列名到 Java 属性
- `foreach` 批量插入
- `LIMIT / OFFSET` 分页查询

## 关键文件

```text
src/main/java/com/eiou/mybatis/practice/MyBatisPracticeDemo.java
src/main/java/com/eiou/mybatis/practice/UserMapper.java
src/main/java/com/eiou/mybatis/practice/DemoUser.java
src/main/resources/application.properties
src/main/resources/mappers/UserMapper.xml
src/main/resources/logback.xml
```
