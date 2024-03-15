package com.zapzook.todoapp.repository;

import com.zapzook.todoapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;

    @BeforeEach
    public void setUp(){
        user = new User(1L, "testname", "test@email.com", "test.png", "test introduce");
        userRepository.save(user);
    }

    @Test
    @DisplayName("findByUsername - 성공(username 존재)")
    void findByUsernameTestSuccess() {
        // given
        String username = "testname";

        // when
        Optional<User> foundUser = userRepository.findByUsername(username);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("findByUsername - 실패(username 존재 X)")
    void findByUsernameTestFail() {
        // given
        String username = "hehehehehehe";

        // when
        Optional<User> foundUser = userRepository.findByUsername(username);

        // then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("findByEmail - 성공(email 존재)")
    void findByEmailTestSuccess() {
        // given
        String email = "test@email.com";
        // when
        Optional<User> foundUser = userRepository.findByEmail(email);

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("findByEmail - 실패(email 존재 X)")
    void findByEmailTestFail() {
        // given
        String email = "hehe@email.com";
        // when
        Optional<User> foundUser = userRepository.findByEmail(email);

        // then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("updateProfile")
    void updateProfileTest() {
        // given
        String profileImage = "update.png";
        String introduce = "Updated introduce";

        // when
        userRepository.updateProfile(user.getId(), introduce, profileImage);
        entityManager.clear(); // 영속성 컨텍스트 동기화
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();

        // then
        assertThat(updatedUser.getProfileImage()).isEqualTo(profileImage);
        assertThat(updatedUser.getIntroduce()).isEqualTo(introduce);
    }
}