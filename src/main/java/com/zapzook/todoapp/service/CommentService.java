package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final Util util;

    public List<CommentResponseDto> getComments(Long todoId) {
        util.findTodo(todoId);
        return commentRepository.findByTodoId(todoId)
                .stream().map(CommentResponseDto::new).toList();
    }

    public void createComment(Long todoId, CommentRequestDto requestDto, User user) {
        Todo todo = util.findTodo(todoId);
        commentRepository.save(new Comment(requestDto.getContents(), todo, user));
    }
    @Transactional
    public void updateComment(Long todoId, Long commentId, CommentRequestDto requestDto, User user) {
        Comment comment = util.findComment(todoId, user, commentId);
        comment.update(requestDto.getContents());
    }

    public void deleteComment(Long todoId, Long commentId, User user) {
        Comment comment = util.findComment(todoId, user, commentId);
        commentRepository.delete(comment);
    }
}
