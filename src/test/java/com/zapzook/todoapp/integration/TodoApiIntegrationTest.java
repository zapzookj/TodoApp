package com.zapzook.todoapp.integration;

import com.zapzook.todoapp.dto.LoginRequestDto;
import com.zapzook.todoapp.dto.LoginResponseDto;
import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.TodoRepository;
import com.zapzook.todoapp.repository.UserRepository;
import com.zapzook.todoapp.service.TodoService;
import com.zapzook.todoapp.service.UserService;
import com.zapzook.todoapp.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 서버의 PORT 를 랜덤으로 설정합니다.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
public class TodoApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    String token;

    @BeforeEach
    public void setUp() {
        User testUser = new User("name", "password", "test@email.com");
        userRepository.save(testUser);

        HttpEntity<LoginRequestDto> request = new HttpEntity<>(new LoginRequestDto("username", "password"));

        ResponseEntity<String> response = restTemplate.postForEntity("/api/login", request, String.class);

        token = response.getHeaders().getFirst(JwtUtil.AUTHORIZATION_HEADER);

        System.out.println(token);
    }

    @AfterEach
    public void cleanup() {
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("할일 카드 생성")
    public void createTodo() {
        // given
        String baseUrl = "http://localhost:" + port;
        TodoRequestDto requestDto = new TodoRequestDto("Todo", "Contents", true);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Object> entity = new HttpEntity<>(requestDto, headers);
        // when
        ResponseEntity<TodoResponseDto> response = restTemplate.postForEntity(
                baseUrl + "/api/todo",
                entity,
                TodoResponseDto.class
        );

        // 검증
//        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals(requestDto.getTitle(), response.getBody().getTitle());
    }


}
