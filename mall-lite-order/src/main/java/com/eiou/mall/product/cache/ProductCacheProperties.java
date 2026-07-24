package com.eiou.mall.product.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "mall.cache.product")
public class ProductCacheProperties {

    private String detailKeyPrefix = "mall:product:detail:";
    private Duration detailTtl = Duration.ofMinutes(30);
    private Duration nullTtl = Duration.ofMinutes(2);
    private Duration ttlJitter = Duration.ofMinutes(5);

    public String getDetailKeyPrefix() {
        return detailKeyPrefix;
    }

    public void setDetailKeyPrefix(String detailKeyPrefix) {
        this.detailKeyPrefix = detailKeyPrefix;
    }

    public Duration getDetailTtl() {
        return detailTtl;
    }

    public void setDetailTtl(Duration detailTtl) {
        this.detailTtl = detailTtl;
    }

    public Duration getNullTtl() {
        return nullTtl;
    }

    public void setNullTtl(Duration nullTtl) {
        this.nullTtl = nullTtl;
    }

    public Duration getTtlJitter() {
        return ttlJitter;
    }

    public void setTtlJitter(Duration ttlJitter) {
        this.ttlJitter = ttlJitter;
    }
}
