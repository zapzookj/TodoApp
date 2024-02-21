package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private Util util;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Todo todo;
    private Comment comment;
    private CommentRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User("user", "password", "user@email.com");
        todo = new Todo();
        todo.setId(1L);
        requestDto = new CommentRequestDto("테스트 댓글");
        comment = new Comment(requestDto, todo, user);
        comment.setId(1L);
    }

    @Test
    @DisplayName("댓글 생성")
    void createComment_Success() {
        // given
        given(util.findTodo(any(Long.class))).willReturn(todo);
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        // when
        CommentResponseDto result = commentService.createComment(todo.getId(), requestDto, user);

        // then
        assertNotNull(result);
        assertEquals(comment.getContents(), result.getContents());
    }

    @Test
    @DisplayName("댓글 수정") // 실패 케이스는 통합 테스트에서 다루자
    void updateComment_Success() {
        // given
        CommentRequestDto updatedRequestDto = new CommentRequestDto("Updated comment.");
        given(util.findComment(todo.getId(), user, comment.getId())).willReturn(comment);

        // when
        CommentResponseDto result = commentService.updateComment(todo.getId(), comment.getId(), updatedRequestDto, user);

        // then
        assertNotNull(result);
        assertEquals(updatedRequestDto.getContents(), result.getContents());
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment_Success() {
        // given
        given(util.findComment(todo.getId(), user, comment.getId())).willReturn(comment);

        // when - then
        assertDoesNotThrow(() -> commentService.deleteComment(todo.getId(), comment.getId(), user));
    }
}