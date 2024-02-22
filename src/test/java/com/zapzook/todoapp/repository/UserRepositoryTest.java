package com.zapzook.todoapp.repository;

import com.zapzook.todoapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        String username = "testname";
        User user = new User();
        user.setUsername(username);
        user.setPassword("testpassword");
        user.setEmail("test@email.com");
        userRepository.save(user);
    }

    @Test
    @DisplayName("findByUsername - 성공(username 존재)")
    void findByUsernameSuccess() {
        // Given
        String username = "testname";

        // When
        Optional<User> foundUser = userRepository.findByUsername(username);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("findByUsername - 실패(username 존재 X)")
    void findByUsernameFail() {
        // Given
        String username = "hehehehehehe";

        // When
        Optional<User> foundUser = userRepository.findByUsername(username);

        // Then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("findByEmail - 성공(email 존재)")
    void findByEmailSuccess() {
        // Given
        String email = "test@email.com";
        // When
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("findByEmail - 실패(email 존재 X)")
    void findByEmailFail() {
        // Given
        String email = "hehe@email.com";
        // When
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isNotPresent();
    }
}