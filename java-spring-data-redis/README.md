# Spring Data Redis API 使用

本模块演示 Spring Data Redis 的最小依赖、最小配置和最直接 API 调用。示例默认连接仓库内 `docker/redis` 提供的本地 Redis 服务。

## 启动 Redis

在仓库根目录执行：

```bash
docker compose --env-file docker/redis/.env -f docker/redis/docker-compose.yml up -d
```

默认连接信息：

- Host：`localhost`
- Port：`6379`
- Password：`redis-pwd`

## 构建

在仓库根目录执行：

```bash
mvn -pl java-spring-data-redis package
```

## 运行示例

```bash
mvn -pl java-spring-data-redis org.codehaus.mojo:exec-maven-plugin:3.6.3:java
```

如需使用其他 Redis 地址，修改 `src/main/resources/application.properties`：

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=redis-pwd
```

## 依赖

- `org.springframework.boot:spring-boot-starter-data-redis`：Spring Data Redis、`StringRedisTemplate` 和默认 Lettuce 客户端

## StringRedisTemplate

```java
public SpringDataRedisApiDemo(StringRedisTemplate redis) {
    this.redis = redis;
}
```

## String

```java
redis.opsForValue().set("spring-data-redis:string", "hello");
String value = redis.opsForValue().get("spring-data-redis:string");
```

## TTL

```java
redis.opsForValue().set("spring-data-redis:ttl", "expires", Duration.ofSeconds(30));
long ttl = redis.getExpire("spring-data-redis:ttl");
```

## Hash

```java
redis.opsForHash().putAll("spring-data-redis:hash", Map.of(
        "name", "spring-data-redis",
        "type", "template"
));
Map<Object, Object> hash = redis.opsForHash().entries("spring-data-redis:hash");
```

## List

```java
redis.opsForList().rightPushAll("spring-data-redis:list", "first", "second", "third");
List<String> values = redis.opsForList().range("spring-data-redis:list", 0, -1);
```

## Set

```java
redis.opsForSet().add("spring-data-redis:set", "java", "redis", "spring-data");
Set<String> members = redis.opsForSet().members("spring-data-redis:set");
```

## Sorted Set

```java
redis.opsForZSet().add("spring-data-redis:zset", "first", 100);
redis.opsForZSet().add("spring-data-redis:zset", "second", 200);
Set<String> ranking = redis.opsForZSet().range("spring-data-redis:zset", 0, -1);
```

## Counter

```java
Long count = redis.opsForValue().increment("spring-data-redis:counter");
```

## Keys

```java
Set<String> keys = redis.keys("spring-data-redis:*");
```

## 示例文件

查看 `src/main/java/com/eiou/spring/data/redis/SpringDataRedisApiDemo.java`。
