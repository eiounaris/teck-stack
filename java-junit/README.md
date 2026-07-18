# JUnit API 使用

本模块只演示 JUnit Jupiter 的常用测试 API。因为 JUnit 本身就是测试框架，所以保留最小测试用例。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-junit test
```

## 依赖

- `org.junit.jupiter:junit-jupiter`：JUnit Jupiter API、参数化测试和执行引擎

## 常用注解

```java
@Test
@DisplayName("display name")
@BeforeEach
@Nested
@ParameterizedTest
@CsvSource({"1, 2, 3"})
```

## 常用断言

```java
assertEquals(3, 1 + 2);
assertTrue(value > 0);
assertAll(
        () -> assertEquals("A", "A"),
        () -> assertTrue(true)
);
assertThrows(IllegalArgumentException.class, () -> {
    throw new IllegalArgumentException("demo");
});
```

## 示例文件

查看 `src/test/java/com/eiou/junit/JUnitApiDemoTest.java`。
