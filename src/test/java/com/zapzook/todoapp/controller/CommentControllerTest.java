package com.zapzook.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zapzook.todoapp.config.WebSecurityConfig;
import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.service.CommentService;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {CommentController.class, TodoController.class, UserController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class CommentControllerTest {
    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private TodoService todoService;

    @MockBean
    private UserService userService;

    private User user;
    private Todo todo;

    private Comment comment;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

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
        todo.setId(1L);
//        this.user = new User("test", "test", "test@email.com");
        this.user = testUser;

        comment = new Comment();
        comment.setId(1L);
        comment.setContents("Test Comment");
        comment.setUser(user);
        comment.setTodo(todo);
        commentRequestDto = new CommentRequestDto("Test Comment");
        commentResponseDto = new CommentResponseDto(1L, "Test Comment", user.getUsername());
    }

    @Test
    @DisplayName("댓글 생성")
    void createCommentTest() throws Exception {
        given(commentService.createComment(anyLong(), any(CommentRequestDto.class), any(User.class)))
                .willReturn(commentResponseDto);
        System.out.println(todo.getId());

        mvc.perform(post("/api/todo/{todoId}/comment", todo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(commentResponseDto.getCommentId()))
                .andExpect(jsonPath("$.contents").value(commentResponseDto.getContents()))
                .andExpect(jsonPath("$.commentWriter").value(commentResponseDto.getCommentWriter()))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정")
    void updateCommentTest() throws Exception {
        given(commentService.updateComment(anyLong(), anyLong(), any(CommentRequestDto.class), any(User.class)))
                .willReturn(commentResponseDto);

        mvc.perform(put("/api/todo/{todoId}/comment/{commentId}", todo.getId(), comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto))
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(commentResponseDto.getCommentId()))
                .andExpect(jsonPath("$.contents").value(commentResponseDto.getContents()))
                .andExpect(jsonPath("$.commentWriter").value(commentResponseDto.getCommentWriter()))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteCommentTest() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyLong(), any(User.class));

        mvc.perform(delete("/api/todo/{todoId}/comment/{commentId}", todo.getId(), comment.getId()).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("삭제 성공."))
                .andDo(print());
    }



}