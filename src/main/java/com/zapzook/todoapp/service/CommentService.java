package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.repository.TodoRepository;
import com.zapzook.todoapp.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
//    private final TodoRepository todoRepository;
    private final Util util;

    public CommentResponseDto createComment(Long todoId, CommentRequestDto requestDto, User user) {
        Todo todo = util.findTodo(todoId);
        Comment comment = commentRepository.save(new Comment(requestDto, todo, user));
        return new CommentResponseDto(comment);
    }
    @Transactional
    public CommentResponseDto updateComment(Long todoId, Long commentId, CommentRequestDto requestDto, User user) {
        Comment comment = util.findComment(todoId, user, commentId);
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }

    public void deleteComment(Long todoId, Long commentId, User user) {
        Comment comment = util.findComment(todoId, user, commentId);
        commentRepository.delete(comment);
    }
}
