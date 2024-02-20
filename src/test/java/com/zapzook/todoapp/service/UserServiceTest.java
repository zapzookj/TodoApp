package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 가입 성공")
    void test1(){
        // given
        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setUsername("testname");
        requestDto.setPassword("testpassword");
        requestDto.setEmail("test@email.com");

        given(userRepository.findByUsername(requestDto.getUsername())).willReturn(Optional.empty());
        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.empty());

        // when
        userService.signup(requestDto);

        // then
        assertDoesNotThrow(() -> userService.signup(requestDto));
    }

    @Test
    @DisplayName("회원 가입 실패 - Username 중복")
    void test2() {
        // given
        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setUsername("testname");
        requestDto.setPassword("testpassword");
        requestDto.setEmail("test@email.com");

        given(userRepository.findByUsername(requestDto.getUsername())).willReturn(Optional.of(new User()));

        // when - then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.signup(requestDto));
        assertEquals("중복된 사용자가 존재합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - email 중복")
    void test3() {
        // given
        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setUsername("testname");
        requestDto.setPassword("testpassword");
        requestDto.setEmail("test@email.com");

        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(new User()));

        // when - then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.signup(requestDto));
        assertEquals("중복된 Email 입니다.", exception.getMessage());
    }
}