package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.exception.NotFoundException;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.repository.CommentRepositoryQueryImpl;
import com.zapzook.todoapp.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentRepositoryQueryImpl commentRepositoryQuery;
    private final Util util;

    public Page<CommentResponseDto> getComments(Long todoId, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return commentRepositoryQuery.findByTodoId(todoId, pageable)
                .map(CommentResponseDto::new);
    }

    public void createComment(Long todoId, CommentRequestDto requestDto, User user) throws NotFoundException {
        Todo todo = util.findTodo(todoId);
        commentRepository.save(new Comment(requestDto.getContents(), todo, user));
    }
    @Transactional
    public void updateComment(Long todoId, Long commentId, CommentRequestDto requestDto, User user) throws NotFoundException {
        Comment comment = util.findComment(todoId, user, commentId);
        comment.update(requestDto.getContents());
    }

    public void deleteComment(Long todoId, Long commentId, User user) throws NotFoundException {
        Comment comment = util.findComment(todoId, user, commentId);
        commentRepository.delete(comment);
    }
}
