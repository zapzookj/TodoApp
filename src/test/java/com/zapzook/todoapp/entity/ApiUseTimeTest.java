package com.zapzook.todoapp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiUseTimeTest {

    @Test
    @DisplayName("ApiUseTime 생성 테스트")
    void createApiUseTimeTest() {
        // given
        User user = new User("username", "password", "email@email.com");
        Long totalTime = 0L;

        // when
        ApiUseTime apiUseTime = new ApiUseTime(user, totalTime);

        // then
        assertNotNull(apiUseTime);
    }

    @Test
    void addUseTime() {
        // given
        User user = new User("username", "password", "email@email.com");
        ApiUseTime apiUseTime = new ApiUseTime(user, 0L);

        // when
        apiUseTime.addUseTime(2L);

        // then
        assertEquals(2L, apiUseTime.getTotalTime());
    }
}