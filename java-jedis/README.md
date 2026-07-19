# Redis Jedis API 使用

本模块演示 Redis Jedis 的最小依赖、最小配置和最直接 API 调用。示例默认连接仓库内 `docker/redis` 提供的本地 Redis 服务。

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
mvn -pl java-jedis package
```

## 运行示例

```bash
mvn -pl java-jedis org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.jedis.JedisApiDemo
```

如需使用其他 Redis 地址，修改 `src/main/resources/application.properties`：

```properties
redis.url=redis://:redis-pwd@localhost:6379
```

## 依赖

- `redis.clients:jedis`：Redis 官方推荐的 Java 客户端之一

## 创建客户端

```java
Properties properties = new Properties();
try (InputStream inputStream = JedisApiDemo.class
        .getClassLoader()
        .getResourceAsStream("application.properties")) {
    properties.load(inputStream);
}

try (RedisClient jedis = RedisClient.create(properties.getProperty("redis.url"))) {
    // call Redis commands
}
```

## String

```java
jedis.set("jedis:string", "hello");
String value = jedis.get("jedis:string");
```

## TTL

```java
jedis.set("jedis:ttl", "expires in 30 seconds", SetParams.setParams().ex(30));
long ttl = jedis.ttl("jedis:ttl");
```

## Hash

```java
jedis.hset("jedis:hash", Map.of(
        "name", "jedis",
        "type", "client"
));
String name = jedis.hget("jedis:hash", "name");
```

## List

```java
jedis.rpush("jedis:list", "first", "second", "third");
List<String> values = jedis.lrange("jedis:list", 0, -1);
```

## Set

```java
jedis.sadd("jedis:set", "java", "redis", "jedis");
Set<String> members = jedis.smembers("jedis:set");
```

## Sorted Set

```java
jedis.zadd("jedis:zset", 100, "first");
jedis.zadd("jedis:zset", 200, "second");
List<String> ranking = jedis.zrange("jedis:zset", 0, -1);
```

## Pipeline

```java
try (AbstractPipeline pipeline = jedis.pipelined()) {
    Response<String> first = pipeline.get("jedis:string");
    Response<Long> count = pipeline.incr("jedis:counter");
    pipeline.sync();
}
```

## Transaction

```java
try (AbstractTransaction transaction = jedis.multi()) {
    transaction.set("jedis:tx", "queued");
    transaction.incr("jedis:counter");
    List<Object> results = transaction.exec();
}
```

## Scan

```java
ScanParams params = new ScanParams().match("jedis:*").count(20);
ScanResult<String> scan = jedis.scan("0", params);
List<String> keys = scan.getResult();
```

## 示例文件

查看 `src/main/java/com/eiou/jedis/JedisApiDemo.java`。
