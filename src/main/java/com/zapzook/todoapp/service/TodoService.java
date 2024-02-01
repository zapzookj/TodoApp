package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.repository.TodoRepository;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;
    private final Util util;
    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto requestDto, User user) {
        Todo todo = todoRepository.save(new Todo(requestDto, user));
        return new TodoResponseDto(todo);
    }

    public TodoResponseDto getTodo(Long todoId, String username) {
        Todo todo = util.findTodo(todoId);
        if(todo.getCompleted()){
            throw new IllegalArgumentException("해당 할일카드는 완료되어 숨김처리 되었습니다.");
        }
        if(!todo.getOpen() && !todo.getUser().getUsername().equals(username)){
            throw new IllegalArgumentException("비공개된 할일카드입니다. 작성자만 조회가 가능합니다.");
        }
        List<Comment> commentList = commentRepository.findByTodoId(todoId);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentResponseDtoList.add(new CommentResponseDto(comment));
        }
        return new TodoResponseDto(todo, commentResponseDtoList);
    }

    public List<TodoResponseDto> getTodoList(String username) {
//        return todoRepository.findAllByCompletedFalseOrderByCreatedAtDesc().stream()
//                .map(TodoResponseDto::new).toList();
        List<Todo> todoList = todoRepository.findAllByCompletedFalseOrderByCreatedAtDesc();
        List<TodoResponseDto> todoResponseDtoList = new ArrayList<>();
        for (Todo todo : todoList) {
            if(todo.getOpen() || todo.getUser().getUsername().equals(username)){
                todoResponseDtoList.add(new TodoResponseDto(todo));
            }
        }
        return todoResponseDtoList;
    }
    @Transactional
    public TodoResponseDto updateTodo(Long todoId, TodoRequestDto requestDto, User user) {
        Todo todo = util.findTodo(todoId, user);
        todo.update(requestDto);
        return new TodoResponseDto(todo);
    }
    @Transactional
    public String completeTodo(Long todoId, User user) {
        Todo todo = util.findTodo(todoId, user);
        todo.complete();
        return ("할일 카드 id : " + todo.getId() + "\n완료 여부 : True");
    }
}
