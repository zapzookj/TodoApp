package com.zapzook.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zapzook.todoapp.config.WebSecurityConfig;
import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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

    private List<CommentResponseDto> commentList = new ArrayList<>();

    private Page<CommentResponseDto> page;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new MockSpringSecurityFilter())
                .build();
    }

    @BeforeEach
    void mockUserSetup() {
        String username = "test";
        String password = "password";
        String email = "email@email.com";
        User testUser = new User(username, password, email);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, null, testUserDetails.getAuthorities());
    }

    void commentListSetup() {
        User user = new User("testname", "testpassword", "test@email.com");
        Todo todo = new Todo("test title", "test contents", true, user);
        Comment comment1 = new Comment("test1", todo, user);
        Comment comment2 = new Comment("test2", todo, user);

        this.commentList.add(new CommentResponseDto(comment1));
        this.commentList.add(new CommentResponseDto(comment2));

        this.page = new PageImpl<>(commentList);
    }

    @Test
    @DisplayName("댓글 생성")
    void createCommentTest() throws Exception {
        // given
        mockUserSetup();
        Long todoId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("test");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when - then
        mvc.perform(post("/api/todo/{todoId}/comment", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 작성 성공"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("댓글 조회")
    void getCommentsTest() throws Exception {
        // given
        mockUserSetup();
        commentListSetup();
        Long todoId = 1L;
        int page = 0;
        int size = 5;
        String sortBy = "contents";
        boolean isAsc = true;

        // when
        given(commentService.getComments(todoId, page, size, sortBy, isAsc)).willReturn(this.page);

        // then
        mvc.perform(get("/api/todo/{todoId}/comment", todoId)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "contents")
                        .param("isAsc", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정")
    void updateCommentTest() throws Exception {
        // given
        mockUserSetup();
        Long todoId = 1L;
        Long commentId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto("test");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        doNothing().when(commentService).updateComment(eq(todoId), eq(commentId), any(CommentRequestDto.class), any(User.class));

        // then
        mvc.perform(put("/api/todo/{todoId}/comment/{commentId}", todoId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 수정 성공"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteCommentTest() throws Exception {
        // given
        mockUserSetup();
        Long todoId = 1L;
        Long commentId = 1L;

        // when
        doNothing().when(commentService).deleteComment(eq(todoId), eq(commentId), any(User.class));

        // then
        mvc.perform(delete("/api/todo/{todoId}/comment/{commentId}", todoId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 삭제 성공"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}