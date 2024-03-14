package com.zapzook.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zapzook.todoapp.config.WebSecurityConfig;
import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.service.TodoService;
import com.zapzook.todoapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private List<TodoResponseDto> todoList = new ArrayList<>();

    private Page<TodoResponseDto> page;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new MockSpringSecurityFilter())
                .build();
    }

    void mockUserSetup() {
        String username = "test";
        String password = "password";
        String email = "email@email.com";
        User testUser = new User(username, password, email);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, null, testUserDetails.getAuthorities());
    }

    void todoListSetup() {
        User user = new User("testname", "testpassword", "test@email.com");
        Todo todo1 = new Todo("Test1 title", "Test1 contents", true, user);
        Todo todo2 = new Todo("Test2 title", "Test2 contents", true, user);

        this.todoList.add(new TodoResponseDto(todo1));
        this.todoList.add(new TodoResponseDto(todo2));

        this.page = new PageImpl<>(todoList);
    }

    @Test
    @DisplayName("할일 카드 생성")
    void createTodoTest() throws Exception {
        // given
        mockUserSetup();
        TodoRequestDto requestDto = new TodoRequestDto("테스트", "테스트", true);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when - then
        mvc.perform(post("/api/todo")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("할일 카드 생성 성공"))
                .andExpect(jsonPath("$.statusCode").value(200));

    }

    @Test
    @DisplayName("특정 할일카드 조회")
    void getTodoTest() throws Exception {
        // given
        mockUserSetup();
        Long todoId = 1L;
        User user = new User("testname", "password", "test@email.com");
        Todo todo = new Todo("title", "contents", true, user);
        TodoResponseDto todoResponseDto = new TodoResponseDto(todo);

        // when
        given(todoService.getTodo(todoId, mockPrincipal.getName())).willReturn(todoResponseDto);

        // then
        mvc.perform(get("/api/todo/{todoId}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("모든 할일 카드 조회")
    void getTodoListTest() throws Exception {
        // given
        mockUserSetup();
        todoListSetup();
        int page = 0;
        int size = 5;
        String sortBy = "title";
        boolean isAsc = true;

        // when
        given(todoService.getTodoList(mockPrincipal.getName(), page, size, sortBy, isAsc)).willReturn(this.page);

        // then
        mvc.perform(get("/api/todos")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "title")
                        .param("isAsc", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("할일 카드 검색")
    void searchTodosTest() throws Exception {
        // given
        mockUserSetup();
        todoListSetup();
        int page = 0;
        int size = 5;
        String sortBy = "title";
        boolean isAsc = true;
        String param = "Test";

        // when
        given(todoService.searchTodos(param, mockPrincipal.getName(), page, size, sortBy, isAsc)).willReturn(this.page);

        // then
        mvc.perform(get("/api/todos/search")
                        .param("param", param)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "title")
                        .param("isAsc", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("자신의 할일 카드 검색")
    void getMyTodosTest() throws Exception {
        // given
        mockUserSetup();
        todoListSetup();

        // when
        given(todoService.getMyTodos(mockPrincipal.getName())).willReturn(todoList);

        // then
        mvc.perform(get("/api/my-todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("특정 할일카드 완료처리")
    void completeTodoTest() throws Exception {
        // given
        mockUserSetup();
        Long todoId = 1L;

        // when
        doNothing().when(todoService).completeTodo(anyLong(), any(User.class));

        // then
        mvc.perform(post("/api/todo/complete/{todoId}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("할일 카드 완료 처리 성공"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("특정 할일카드 수정")
    void updateTodoTest() throws Exception {
        // given
        mockUserSetup();
        Long todoId = 1L;
        TodoRequestDto requestDto = new TodoRequestDto("수정된 제목", "수정된 내용", true);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        doNothing().when(todoService).updateTodo(eq(todoId), any(TodoRequestDto.class), any(User.class));

        // then
        mvc.perform(put("/api/todo/{todoId}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("할일 카드 수정 성공"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}