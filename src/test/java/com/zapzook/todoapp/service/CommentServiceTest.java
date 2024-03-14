package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.repository.CommentRepositoryQueryImpl;
import com.zapzook.todoapp.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentRepositoryQueryImpl commentRepositoryQuery;

    @Mock
    private Util util;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Todo todo;
    private Comment comment;
    private CommentRequestDto requestDto;
    private List<Comment> commentList = new ArrayList<>();
    private Page<Comment> page;

    @BeforeEach
    void setUp() {
        user = new User("testname", "testpassword", "test@email.com");
        todo = new Todo("title", "contents", false, user);
        todo.setId(1L);
        requestDto = new CommentRequestDto("test");
        comment = new Comment("test", todo, user);
    }

    void commentListSetUp() {
        Comment comment1 = new Comment("contents1", todo, user);
        Comment comment2 = new Comment("contents2", todo, user);

        this.commentList.add(comment);
        this.commentList.add(comment1);
        this.commentList.add(comment2);

        this.page = new PageImpl<>(commentList);
    }

    @Test
    @DisplayName("댓글 생성")
    void createCommentTest() {
        // given
        given(util.findTodo(any(Long.class))).willReturn(todo);

        // when
        commentService.createComment(todo.getId(), requestDto, user);

        // then
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 조회")
    void getCommentsTest() {
        // given
        commentListSetUp();
        int pageNum = 0;
        int size = 5;
        String sortBy = "title";
        boolean isAsc = true;
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        given(commentRepositoryQuery.findByTodoId(todo.getId(), pageable)).willReturn(page);

        // when
        Page<CommentResponseDto> result = commentService.getComments(todo.getId(), pageNum, size, sortBy, isAsc);

        // then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
    }

    @Test
    @DisplayName("댓글 수정")
    void updateCommentTest() throws Exception {
        // given
        CommentRequestDto updatedRequestDto = new CommentRequestDto("Updated Contents");
        given(util.findComment(todo.getId(), user, comment.getId())).willReturn(comment);

        // when
        commentService.updateComment(todo.getId(), comment.getId(), updatedRequestDto, user);

        // then
        assertEquals("Updated Contents", comment.getContents());
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteCommentTest() throws Exception {
        // given
        given(util.findComment(todo.getId(), user, comment.getId())).willReturn(comment);

        // when
        commentService.deleteComment(todo.getId(), comment.getId(), user);

        // then
        verify(commentRepository).delete(comment);
    }
}