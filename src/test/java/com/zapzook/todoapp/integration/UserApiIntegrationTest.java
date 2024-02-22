package com.zapzook.todoapp.integration;

import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.UserRepository;
import com.zapzook.todoapp.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 서버의 PORT 를 랜덤으로 설정합니다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
public class UserApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        User testUser = new User("name", "password", "test@email.com");
        userRepository.save(testUser);
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입 - 성공")
    void SignupSuccess() {
        // given
        String baseUrl = "http://localhost:" + port;
        SignupRequestDto requestDto = new SignupRequestDto("test", "password", "email@email.com");

        // when
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/api/user/signup", requestDto, String.class);
        User foundUser = userRepository.findByUsername(requestDto.getUsername()).orElse(null);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(foundUser);
        assertEquals(requestDto.getEmail(), foundUser.getEmail());
    }

    @Test
    @DisplayName("회원 가입 - 실패(username 중복 or email 중복)")
    void SignupFail_1() {
        // given
        String baseUrl = "http://localhost:" + port;
        SignupRequestDto requestDto1 = new SignupRequestDto("name", "password", "email@email.com"); // username 중복
        SignupRequestDto requestDto2 = new SignupRequestDto("test", "password", "test@email.com"); // email 중복

        // when
        ResponseEntity<String> response1 = restTemplate.postForEntity(baseUrl + "/api/user/signup", requestDto1, String.class);
        ResponseEntity<String> response2 = restTemplate.postForEntity(baseUrl + "/api/user/signup", requestDto2, String.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertTrue(response1.getBody().contains("중복된 사용자가 존재합니다."));
        assertTrue(response2.getBody().contains("중복된 Email 입니다."));
    }

    @Test
    @DisplayName("회원 가입 - 실패(Validation 오류)")
    void SignupFail_2() {
        // given
        String baseUrl = "http://localhost:" + port;
        SignupRequestDto requestDto = new SignupRequestDto("name@!$!@#", "password", "email@email.com");

        // when
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/api/user/signup", requestDto, String.class);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("회원가입 실패!"));
    }
}


