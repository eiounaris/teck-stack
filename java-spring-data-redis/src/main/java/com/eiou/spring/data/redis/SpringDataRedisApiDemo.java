package com.eiou.spring.data.redis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class SpringDataRedisApiDemo implements CommandLineRunner {
    private static final String STRING_KEY = "spring-data-redis:string";
    private static final String TTL_KEY = "spring-data-redis:ttl";
    private static final String HASH_KEY = "spring-data-redis:hash";
    private static final String LIST_KEY = "spring-data-redis:list";
    private static final String SET_KEY = "spring-data-redis:set";
    private static final String ZSET_KEY = "spring-data-redis:zset";
    private static final String COUNTER_KEY = "spring-data-redis:counter";

    private final StringRedisTemplate redis;

    public SpringDataRedisApiDemo(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringDataRedisApiDemo.class, args);
        context.close();
    }

    @Override
    public void run(String... args) {
        redis.delete(List.of(
                STRING_KEY,
                TTL_KEY,
                HASH_KEY,
                LIST_KEY,
                SET_KEY,
                ZSET_KEY,
                COUNTER_KEY
        ));

        redis.opsForValue().set(STRING_KEY, "hello");
        System.out.println("string: " + redis.opsForValue().get(STRING_KEY));

        redis.opsForValue().set(TTL_KEY, "expires in 30 seconds", Duration.ofSeconds(30));
        System.out.println("ttl: " + redis.getExpire(TTL_KEY));

        redis.opsForHash().putAll(HASH_KEY, Map.of(
                "name", "spring-data-redis",
                "type", "template"
        ));
        System.out.println("hash: " + redis.opsForHash().entries(HASH_KEY));

        redis.opsForList().rightPushAll(LIST_KEY, "first", "second", "third");
        List<String> list = redis.opsForList().range(LIST_KEY, 0, -1);
        System.out.println("list: " + list);

        redis.opsForSet().add(SET_KEY, "java", "redis", "spring-data");
        Set<String> set = redis.opsForSet().members(SET_KEY);
        System.out.println("set: " + set);

        redis.opsForZSet().add(ZSET_KEY, "first", 100);
        redis.opsForZSet().add(ZSET_KEY, "second", 200);
        Set<String> ranking = redis.opsForZSet().range(ZSET_KEY, 0, -1);
        System.out.println("zset: " + ranking);

        Long count = redis.opsForValue().increment(COUNTER_KEY);
        System.out.println("counter: " + count);

        Set<String> keys = redis.keys("spring-data-redis:*");
        System.out.println("keys: " + keys);
    }
}
