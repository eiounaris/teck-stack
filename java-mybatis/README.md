# MyBatis API 使用

本模块演示 MyBatis 的最小依赖、最小配置和最直接 API 调用。示例使用 H2 内存数据库，并用 `Map` 传参/接收结果，避免引入实体类和 Mapper 接口。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-mybatis package
```

## 运行示例

```bash
mvn -pl java-mybatis org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.mybatis.MyBatisApiDemo
```

## 依赖

- `org.mybatis:mybatis`：MyBatis 核心 API
- `com.h2database:h2`：内存数据库驱动，方便本地直接运行

## SqlSessionFactory

```java
try (Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
}
```

## SqlSession

```java
try (SqlSession session = sqlSessionFactory.openSession()) {
    // call mapped statements
}
```

## insert / update

```java
session.insert("mybatisDemo.insertRow", Map.of(
        "name", "first",
        "amount", new BigDecimal("10.00")
));

session.update("mybatisDemo.updateAmount", Map.of(
        "name", "first",
        "delta", new BigDecimal("5.00")
));

session.commit();
```

## selectList

```java
List<Map<String, Object>> rows = session.selectList("mybatisDemo.selectRows");
```

## 最小配置

配置文件位于：

```text
src/main/resources/mybatis-config.xml
```

Mapper XML 位于：

```text
src/main/resources/mappers/MyBatisDemoMapper.xml
```

## 示例文件

查看 `src/main/java/com/eiou/mybatis/MyBatisApiDemo.java`。
