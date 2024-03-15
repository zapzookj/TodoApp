package com.zapzook.todoapp.repository;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataRedisTest
class RefreshTokenRedisRepositoryTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @BeforeEach
    void setUp() {
        refreshTokenRedisRepository = new RefreshTokenRedisRepository();
    }

    @Test
    @DisplayName("데이터 저장 및 조회")
    void saveAndFindTest() {
        // given
        String key = "Key";
        String value = "Value";

        // when
        refreshTokenRedisRepository.save(key, value);
        String foundValue = refreshTokenRedisRepository.findByKey(key);

        // then
        assertThat(foundValue).isEqualTo(value);
    }

    @Test
    void existsByKey() {
    }
}