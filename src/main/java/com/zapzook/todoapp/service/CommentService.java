package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TodoRepository todoRepository;

    public CommentResponseDto createComment(Long todoId, CommentRequestDto requestDto, User user) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new IllegalArgumentException("해당 할일카드가 존재하지 않습니다.")
        );
        Comment comment = commentRepository.save(new Comment(requestDto, todo, user));
        return new CommentResponseDto(comment);
    }
    @Transactional
    public CommentResponseDto updateComment(Long todoId, Long commentId, CommentRequestDto requestDto, User user) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new IllegalArgumentException("해당 할일카드가 존재하지 않습니다.")
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
        );
        if(!user.getUsername().equals(comment.getUser().getUsername())){
            throw new IllegalArgumentException("username이 일치하지 않습니다!");
        }
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }

    public void deleteComment(Long todoId, Long commentId, User user) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new IllegalArgumentException("해당 할일카드가 존재하지 않습니다.")
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
        );
        if(!user.getUsername().equals(comment.getUser().getUsername())){
            throw new IllegalArgumentException("username이 일치하지 않습니다!");
        }
        commentRepository.delete(comment);
    }
}
