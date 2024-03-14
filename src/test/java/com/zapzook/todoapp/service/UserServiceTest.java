package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.dto.UserRequestDto;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.UserRepository;
import com.zapzook.todoapp.util.JwtUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private S3Client s3Client;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequestDto requestDto;


    void requestSetUp() {
        user = new User(1L, "username", "email@example.com", "oldProfileImageUrl", "introduce");
        requestDto = new UserRequestDto(new MockMultipartFile("profileImage", "profile.jpg", "image/jpeg", "image data".getBytes()), "new introduce");
    }

    @Test
    @DisplayName("회원 가입 성공")
    void signupTestSuccess(){
        // given
        SignupRequestDto requestDto = new SignupRequestDto();
        requestDto.setUsername("testname");
        requestDto.setPassword("password");
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
    void signupTestFail1() {
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
    void signupTestFail2() {
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

    @Test
    @Disabled
    @DisplayName("회원 프로필 등록")
    void setProfileTest() throws Exception { // 수정 필요
        // given
        requestSetUp();
        String expectProfileImageUrl = "https://test-s3-url.com/profile-images/test.png";
        String bucket = "my-bucket";
        String key = "test-key";
        given(s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucket).key(key).build())).willReturn(new URI(expectProfileImageUrl).toURL());
        given(jwtUtil.createToken(anyString(), anyLong(), anyString(), anyString(), anyString())).willReturn("newToken");

        // when
        String newToken = userService.setProfile(user, requestDto);

        // then
        assertNotNull(newToken);
        assertEquals("newToken", newToken);

    }
}