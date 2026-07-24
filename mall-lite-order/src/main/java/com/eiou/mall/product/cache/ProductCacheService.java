package com.eiou.mall.product.cache;

import com.eiou.mall.product.dto.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProductCacheService {

    private static final Logger log = LoggerFactory.getLogger(ProductCacheService.class);
    private static final String NULL_VALUE = "__NULL__";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProductCacheProperties properties;

    public ProductCacheService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            ProductCacheProperties properties
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public CacheLookup getDetail(Long productId) {
        String key = detailKey(productId);
        String value;
        try {
            value = redisTemplate.opsForValue().get(key);
        } catch (RuntimeException exception) {
            log.warn("Read product detail cache failed, key={}", key, exception);
            return CacheLookup.miss();
        }

        if (!StringUtils.hasText(value)) {
            return CacheLookup.miss();
        }
        if (NULL_VALUE.equals(value)) {
            return CacheLookup.hit(null);
        }

        try {
            return CacheLookup.hit(objectMapper.readValue(value, ProductResponse.class));
        } catch (JsonProcessingException exception) {
            log.warn("Parse product detail cache failed, key={}", key, exception);
            evictDetail(productId);
            return CacheLookup.miss();
        }
    }

    public void cacheDetail(ProductResponse product) {
        String key = detailKey(product.id());
        try {
            String value = objectMapper.writeValueAsString(product);
            redisTemplate.opsForValue().set(key, value, ttlWithJitter());
        } catch (RuntimeException | JsonProcessingException exception) {
            log.warn("Write product detail cache failed, key={}", key, exception);
        }
    }

    public void cacheNull(Long productId) {
        String key = detailKey(productId);
        try {
            redisTemplate.opsForValue().set(key, NULL_VALUE, properties.getNullTtl());
        } catch (RuntimeException exception) {
            log.warn("Write null product detail cache failed, key={}", key, exception);
        }
    }

    public void evictDetail(Long productId) {
        String key = detailKey(productId);
        try {
            redisTemplate.delete(key);
        } catch (RuntimeException exception) {
            log.warn("Delete product detail cache failed, key={}", key, exception);
        }
    }

    private Duration ttlWithJitter() {
        long jitterSeconds = Math.max(0, properties.getTtlJitter().toSeconds());
        if (jitterSeconds == 0) {
            return properties.getDetailTtl();
        }
        long randomSeconds = ThreadLocalRandom.current().nextLong(jitterSeconds + 1);
        return properties.getDetailTtl().plusSeconds(randomSeconds);
    }

    private String detailKey(Long productId) {
        return properties.getDetailKeyPrefix() + productId;
    }

    public record CacheLookup(boolean hit, ProductResponse product) {

        static CacheLookup hit(ProductResponse product) {
            return new CacheLookup(true, product);
        }

        static CacheLookup miss() {
            return new CacheLookup(false, null);
        }
    }
}
