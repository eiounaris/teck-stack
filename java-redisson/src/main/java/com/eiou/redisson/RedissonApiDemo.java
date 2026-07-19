package com.eiou.redisson;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class RedissonApiDemo {
    private static final String CONFIG_FILE = "application.properties";
    private static final String REDIS_ADDRESS_PROPERTY = "redis.address";

    private RedissonApiDemo() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Config config = readConfig();

        RedissonClient redisson = Redisson.create(config);
        try {
            redisson.getKeys().delete(
                    "redisson:bucket",
                    "redisson:ttl",
                    "redisson:map",
                    "redisson:list",
                    "redisson:set",
                    "redisson:zset",
                    "redisson:counter",
                    "redisson:lock"
            );

            RBucket<String> bucket = redisson.getBucket("redisson:bucket");
            bucket.set("hello");
            System.out.println("bucket: " + bucket.get());

            RBucket<String> ttlBucket = redisson.getBucket("redisson:ttl");
            ttlBucket.set("expires in 30 seconds", Duration.ofSeconds(30));
            System.out.println("ttl: " + ttlBucket.remainTimeToLive());

            RMap<String, String> map = redisson.getMap("redisson:map");
            map.put("name", "redisson");
            map.put("type", "client");
            System.out.println("map: " + map.readAllMap());

            RList<String> list = redisson.getList("redisson:list");
            list.addAll(List.of("first", "second", "third"));
            System.out.println("list: " + list.readAll());

            RSet<String> set = redisson.getSet("redisson:set");
            set.add("java");
            set.add("redis");
            set.add("redisson");
            System.out.println("set: " + set.readAll());

            RScoredSortedSet<String> ranking = redisson.getScoredSortedSet("redisson:zset");
            ranking.add(100, "first");
            ranking.add(200, "second");
            System.out.println("zset: " + ranking.readAll());

            RAtomicLong counter = redisson.getAtomicLong("redisson:counter");
            System.out.println("counter: " + counter.incrementAndGet());

            RLock lock = redisson.getLock("redisson:lock");
            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) {
                try {
                    System.out.println("lock: acquired");
                } finally {
                    lock.unlock();
                }
            }

            List<String> keys = new ArrayList<>();
            redisson.getKeys()
                    .getKeys(KeysScanOptions.defaults().pattern("redisson:*"))
                    .forEach(keys::add);
            System.out.println("keys: " + keys);
        } finally {
            redisson.shutdown();
        }
    }

    private static Config readConfig() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = RedissonApiDemo.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing classpath resource: " + CONFIG_FILE);
            }

            properties.load(inputStream);
        }

        String address = properties.getProperty(REDIS_ADDRESS_PROPERTY);
        if (address == null || address.isBlank()) {
            throw new IllegalStateException("Missing property: " + REDIS_ADDRESS_PROPERTY);
        }

        Config config = new Config();
        config.setCodec(StringCodec.INSTANCE);
        config.useSingleServer().setAddress(address);

        return config;
    }
}
