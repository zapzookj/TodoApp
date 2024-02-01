package com.zapzook.todoapp.util;

import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Component
public class Util {
    public final TodoRepository todoRepository;
    private final CommentRepository commentRepository;

    public Todo findTodo(Long todoId){
        return todoRepository.findById(todoId).orElseThrow(
                () -> new IllegalArgumentException("해당 할일카드가 존재하지 않습니다.")
        );
    }

    public Todo findTodo(Long todoId, User user){
        Todo todo = findTodo(todoId);
        if(!user.getUsername().equals(todo.getUser().getUsername())){
            throw new IllegalArgumentException("username이 일치하지 않습니다!");
        }
        return todo;
    }

    public Comment findComment(Long todoId, User user, Long commentId){
        Todo todo = findTodo(todoId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다.")
        );
        if(!user.getUsername().equals(comment.getUser().getUsername())){
            throw new IllegalArgumentException("username이 일치하지 않습니다!");
        }
        return comment;
    }


}
