# Mockito API 使用

本模块只演示 Mockito 的常用测试 API。因为 Mockito 本身是测试框架，所以保留最小测试用例。

## 构建

在仓库根目录执行：

```bash
mvn -pl java-mockito test
```

## 依赖

- `org.junit.jupiter:junit-jupiter`：测试运行
- `org.mockito:mockito-junit-jupiter`：Mockito 与 JUnit Jupiter 集成

## 常用注解

```java
@ExtendWith(MockitoExtension.class)
@Mock
@InjectMocks
@Captor
```

## 常用 API

```java
when(dependency.load("key")).thenReturn("value");
when(dependency.load("error")).thenThrow(new IllegalStateException("demo"));

verify(dependency).save("value");
verify(dependency, never()).save("error");

ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
verify(dependency).save(captor.capture());

List<String> list = spy(new ArrayList<>());
```

## 示例文件

查看 `src/test/java/com/eiou/mockito/MockitoApiDemoTest.java`。
