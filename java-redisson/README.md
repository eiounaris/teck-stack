# Redis Redisson API 使用

本模块演示 Redis Redisson 的最小依赖、最小配置和最直接 API 调用。示例默认连接仓库内 `docker/redis` 提供的本地 Redis 服务。

## 启动 Redis

在仓库根目录执行：

```bash
docker compose --env-file docker/redis/.env -f docker/redis/docker-compose.yml up -d
```

默认连接信息：

- Address：`redis://:redis-pwd@localhost:6379`

## 构建

在仓库根目录执行：

```bash
mvn -pl java-redisson package
```

## 运行示例

```bash
mvn -pl java-redisson org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.eiou.redisson.RedissonApiDemo
```

如需使用其他 Redis 地址，修改 `src/main/resources/application.properties`：

```properties
redis.address=redis://:redis-pwd@localhost:6379
```

## 依赖

- `org.redisson:redisson`：Redis Java 客户端，提供分布式对象、集合和锁等 API

## Config

```java
Config config = new Config();
config.setCodec(StringCodec.INSTANCE);
config.useSingleServer()
        .setAddress("redis://:redis-pwd@localhost:6379");
```

## RedissonClient

```java
RedissonClient redisson = Redisson.create(config);
try {
    // call Redisson APIs
} finally {
    redisson.shutdown();
}
```

## Bucket

```java
RBucket<String> bucket = redisson.getBucket("redisson:bucket");
bucket.set("hello");
String value = bucket.get();
```

## TTL

```java
RBucket<String> ttlBucket = redisson.getBucket("redisson:ttl");
ttlBucket.set("expires in 30 seconds", Duration.ofSeconds(30));
long ttl = ttlBucket.remainTimeToLive();
```

## Map

```java
RMap<String, String> map = redisson.getMap("redisson:map");
map.put("name", "redisson");
map.put("type", "client");
Map<String, String> values = map.readAllMap();
```

## List

```java
RList<String> list = redisson.getList("redisson:list");
list.addAll(List.of("first", "second", "third"));
List<String> values = list.readAll();
```

## Set

```java
RSet<String> set = redisson.getSet("redisson:set");
set.add("java");
set.add("redis");
set.add("redisson");
Set<String> members = set.readAll();
```

## Sorted Set

```java
RScoredSortedSet<String> ranking = redisson.getScoredSortedSet("redisson:zset");
ranking.add(100, "first");
ranking.add(200, "second");
Collection<String> values = ranking.readAll();
```

## AtomicLong

```java
RAtomicLong counter = redisson.getAtomicLong("redisson:counter");
long value = counter.incrementAndGet();
```

## Lock

```java
RLock lock = redisson.getLock("redisson:lock");
if (lock.tryLock(1, 5, TimeUnit.SECONDS)) {
    try {
        // protected block
    } finally {
        lock.unlock();
    }
}
```

## Keys

```java
Iterable<String> keys = redisson.getKeys()
        .getKeys(KeysScanOptions.defaults().pattern("redisson:*"));
```

## 示例文件

查看 `src/main/java/com/eiou/redisson/RedissonApiDemo.java`。
