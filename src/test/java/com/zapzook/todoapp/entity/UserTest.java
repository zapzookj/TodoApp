package com.zapzook.todoapp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    @DisplayName("User 생성 테스트")
    void createUserTest() {
        // given
        String username = "username";
        String password = "password";
        String email = "email@email.com";

        // when
        User user = new User(username, password, email);

        // then
        assertNotNull(user);
        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("email@email.com", user.getEmail());
    }

    @Test
    @DisplayName("User update 테스트")
    void updateTest() {
        // given
        User user = new User("username", "password", "email@email.com");

        // when
        user.update("introduce", "profileImage");

        // then
        assertEquals("introduce", user.getIntroduce());
        assertEquals("profileImage", user.getProfileImage());
    }
}