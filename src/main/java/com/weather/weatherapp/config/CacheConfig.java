package com.weather.weatherapp.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
                new CaffeineCache("currentWeather",
                        Caffeine.newBuilder()
                                .maximumSize(1000)
                                .expireAfterWrite(10, TimeUnit.MINUTES)
                                .refreshAfterWrite(5, TimeUnit.MINUTES)
                                .recordStats()
                                .build()),
                new CaffeineCache("forecast",
                        Caffeine.newBuilder()
                                .maximumSize(500)
                                .expireAfterWrite(60, TimeUnit.MINUTES)
                                //.refreshAfterWrite(5, TimeUnit.MINUTES)
                                .recordStats()
                                .build())
        ));
        return cacheManager;
    }
}
