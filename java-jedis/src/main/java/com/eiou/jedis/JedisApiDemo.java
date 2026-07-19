package com.eiou.jedis;

import redis.clients.jedis.AbstractPipeline;
import redis.clients.jedis.AbstractTransaction;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.resps.ScanResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class JedisApiDemo {
    private static final String CONFIG_FILE = "application.properties";
    private static final String REDIS_URL_PROPERTY = "redis.url";

    private JedisApiDemo() {
    }

    public static void main(String[] args) throws IOException {
        String redisUrl = readRedisUrl();

        try (RedisClient jedis = RedisClient.create(redisUrl)) {
            jedis.del(
                    "jedis:string",
                    "jedis:ttl",
                    "jedis:hash",
                    "jedis:list",
                    "jedis:set",
                    "jedis:zset",
                    "jedis:counter",
                    "jedis:tx"
            );

            jedis.set("jedis:string", "hello");
            System.out.println("string: " + jedis.get("jedis:string"));

            jedis.set("jedis:ttl", "expires in 30 seconds", SetParams.setParams().ex(30));
            System.out.println("ttl: " + jedis.ttl("jedis:ttl"));

            jedis.hset("jedis:hash", Map.of(
                    "name", "jedis",
                    "type", "client"
            ));
            System.out.println("hash: " + jedis.hgetAll("jedis:hash"));

            jedis.rpush("jedis:list", "first", "second", "third");
            List<String> list = jedis.lrange("jedis:list", 0, -1);
            System.out.println("list: " + list);

            jedis.sadd("jedis:set", "java", "redis", "jedis");
            Set<String> set = jedis.smembers("jedis:set");
            System.out.println("set: " + set);

            jedis.zadd("jedis:zset", 100, "first");
            jedis.zadd("jedis:zset", 200, "second");
            List<String> ranking = jedis.zrange("jedis:zset", 0, -1);
            System.out.println("zset: " + ranking);

            try (AbstractPipeline pipeline = jedis.pipelined()) {
                Response<String> first = pipeline.get("jedis:string");
                Response<Long> count = pipeline.incr("jedis:counter");
                pipeline.sync();

                System.out.println("pipeline get: " + first.get());
                System.out.println("pipeline incr: " + count.get());
            }

            try (AbstractTransaction transaction = jedis.multi()) {
                transaction.set("jedis:tx", "queued");
                transaction.incr("jedis:counter");
                List<Object> results = transaction.exec();

                System.out.println("transaction: " + results);
            }

            ScanParams params = new ScanParams()
                    .match("jedis:*")
                    .count(20);
            ScanResult<String> scan = jedis.scan("0", params);

            System.out.println("scan: " + scan.getResult());
        }
    }

    private static String readRedisUrl() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = JedisApiDemo.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing classpath resource: " + CONFIG_FILE);
            }

            properties.load(inputStream);
        }

        String redisUrl = properties.getProperty(REDIS_URL_PROPERTY);
        if (redisUrl == null || redisUrl.isBlank()) {
            throw new IllegalStateException("Missing property: " + REDIS_URL_PROPERTY);
        }

        return redisUrl;
    }
}
