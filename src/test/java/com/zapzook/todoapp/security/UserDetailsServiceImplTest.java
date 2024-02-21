package com.zapzook.todoapp.security;

import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private com.zapzook.todoapp.entity.User user;

    @BeforeEach
    void setUp() {
        user = new User("user1", "password", "fds@naver.com");
    }

    @Test
    @DisplayName("사용자 이름으로 UserDetails 로드 - 성공")
    void loadUserByUsername_Success() {
        // given
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // then
        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
    }

    @Test
    @DisplayName("사용자 이름으로 UserDetails 로드 - 실패 (사용자 이름이 존재하지 않음)")
    void loadUserByUsername_NotFound() {
        // given
        String username = "nonexistentUser";
        given(userRepository.findByUsername(username)).willThrow(new UsernameNotFoundException("Not Found " + username));

        // when
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));

        // then
        assertEquals("Not Found " + username, exception.getMessage());
    }
}