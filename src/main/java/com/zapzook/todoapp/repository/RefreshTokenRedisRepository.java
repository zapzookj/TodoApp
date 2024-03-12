package com.zapzook.todoapp.repository;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRedisRepository {
    private static final long EXPIRE_TIME = 6 * 60 * 60; // 6시간

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOperations;

    public void save(String key, String value) {
        valueOperations.set(key, value, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    public String findByKey(String key) {
        return valueOperations.get(key);
    }

    public Boolean existsByKey(String key) {
        return valueOperations.getOperations().hasKey(key);
    }

    public void delete(String key) {
        valueOperations.getOperations().delete(key);
    }
}
