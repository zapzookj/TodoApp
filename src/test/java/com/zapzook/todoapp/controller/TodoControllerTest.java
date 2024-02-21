package com.zapzook.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zapzook.todoapp.config.WebSecurityConfig;
import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.RefreshTokenRepository;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.security.UserDetailsServiceImpl;
import com.zapzook.todoapp.service.TodoService;
import com.zapzook.todoapp.service.UserService;
import com.zapzook.todoapp.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(
        controllers = {TodoController.class, UserController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class TodoControllerTest {
    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @MockBean
    private UserService userService;


    private User user;
    private Todo todo;
    private List<TodoResponseDto> todoList = new ArrayList<>();

    @BeforeEach
    void mockUserSetup() {
        // Mock 테스트 유져 생성
        String username = "name";
        String password = "password";
        String email = "test@email.com";
        User testUser = new User(username, password, email);
//        testUser.setId(1L);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, null, null);
        TodoRequestDto requestDto = new TodoRequestDto("테스트 제목", "테스트 내용", true);
        todo = new Todo(requestDto, testUser);
//        this.user = new User("test", "test", "test@email.com");
        this.user = testUser;
    }

    void todoSetup(){
        Todo todo1 = new Todo(new TodoRequestDto("Test1 title", "Test1 contents", true), user);
        Todo todo2 = new Todo(new TodoRequestDto("Test2 title", "Test2 contents", true), user);

        this.todoList.add(new TodoResponseDto(todo1));
        this.todoList.add(new TodoResponseDto(todo2));
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }
    @Test
    @DisplayName("할일 카드 생성")
    void createTodoTest() throws Exception {
        // given
        TodoRequestDto requestDto = new TodoRequestDto("테스트", "테스트", true);
        String todoInfo = objectMapper.writeValueAsString(requestDto);

        // when - then
        mvc.perform(post("/api/todo")
                .content(todoInfo)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("특정 할일카드 조회")
    void getTodoTest() throws Exception {
        // given
        this.mockUserSetup();
        Long todoId = 1L;
        todo.setId(todoId);
        TodoResponseDto todoResponseDto = new TodoResponseDto(todo);
        given(todoService.getTodo(todoId, mockPrincipal.getName())).willReturn(todoResponseDto);
        // when - then
        mvc.perform(get("/api/todo/{todoId}", todoId)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @DisplayName("모든 할일 카드 조회")
    void test3() throws Exception {
        // given
        todoSetup();
        given(todoService.getTodoList(mockPrincipal.getName())).willReturn(todoList);
        System.out.println(todoList);

        // when - then
        mvc.perform(get("/api/todo").principal(mockPrincipal))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").exists())
                .andDo(print());

    }

    @Test
    @DisplayName("할일 카드 검색")
    void test4() throws Exception {
        // given
        todoSetup();
        String param = "title";
        given(todoService.searchTodo(param, mockPrincipal.getName())).willReturn(todoList);
        // when - then
        mvc.perform(get("/api/todo/search")
                        .principal(mockPrincipal)
                        .param("param", param)
                )
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("특정 할일카드 완료처리") // 반환값에 ResponseEntity를 사용하지 않아서 일일이 given을 사용해야한다.. 불편
    void completeTodoTest() throws Exception {
        // given
        Long todoId = 1L;
        String expectedResponse = "할일 카드가 완료 처리됨";
        given(todoService.completeTodo(todoId, user)).willReturn(expectedResponse);

        // when - then
        mvc.perform(post("/api/todo/{todoId}", todoId).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse))
                .andDo(print());
    }

    @Test
    @DisplayName("특정 할일카드 수정") // 또 응답이 반환되지 않는다. 어디서는 되고 어디서는 안되고 도대체 원인이 뭐야?
    void updateTodoTest() throws Exception {
        // given
        Long todoId = 1L;
        TodoRequestDto requestDto = new TodoRequestDto("수정된 제목", "수정된 내용", true);
        TodoResponseDto responseDto = new TodoResponseDto(); // 필요한 값 설정
        responseDto.setId(todoId);
        responseDto.setTitle(requestDto.getTitle());
        responseDto.setContents(requestDto.getContents());
        responseDto.setOpen(requestDto.getOpen());

        given(todoService.updateTodo(todoId, requestDto, user)).willReturn(responseDto);

        // when - then
        mvc.perform(put("/api/todo/{todoId}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(todoId))
//                .andExpect(jsonPath("$.title").value(requestDto.getTitle()))
//                .andExpect(jsonPath("$.contents").value(requestDto.getContents()))
//                .andExpect(jsonPath("$.open").value(requestDto.getOpen()))
                .andDo(print());
    }


}