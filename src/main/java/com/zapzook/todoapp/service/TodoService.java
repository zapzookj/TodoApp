package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.exception.NotFoundException;
import com.zapzook.todoapp.repository.CommentRepository;
import com.zapzook.todoapp.repository.TodoRepository;
import com.zapzook.todoapp.repository.TodoRepositoryQueryImpl;
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
    private final TodoRepositoryQueryImpl todoRepositoryQuery;
    private final Util util;
    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto requestDto, User user) {
        Todo todo = todoRepository.save(new Todo(requestDto, user));
        return new TodoResponseDto(todo);
    }

    public TodoResponseDto getTodo(Long todoId, String username) throws NotFoundException {
        Todo todo = util.findTodo(todoId);
        if(todo.getCompleted()){
            throw new IllegalArgumentException("해당 할일카드는 완료되어 숨김처리 되었습니다.");
        }
        if(!todo.getOpen() && !todo.getUser().getUsername().equals(username)){
            throw new IllegalArgumentException("비공개된 할일카드입니다. 작성자만 조회가 가능합니다.");
        }
        return new TodoResponseDto(todo);
    }

    public List<TodoResponseDto> getTodoList(String username) {
//        System.out.println("ㅎ2");
//        List<TodoResponseDto> todoResponseDtoList = todoRepository.findAllByCompletedFalseAndOpenTrueOrUserUsernameOrderByCreatedAtDesc(username)
//                .stream().map(TodoResponseDto::new).toList();
//        System.out.println("ㅎ3");
//        return todoResponseDtoList;
        return todoRepositoryQuery.findAllByUserName(username).stream().map(TodoResponseDto::new).toList();
    }
    @Transactional
    public void updateTodo(Long todoId, TodoRequestDto requestDto, User user) throws NotFoundException {
        Todo todo = util.findTodo(todoId, user);
        todo.update(requestDto);
    }
    @Transactional
    public void completeTodo(Long todoId, User user) throws NotFoundException {
        Todo todo = util.findTodo(todoId, user);
        todo.complete();
    }

    public List<TodoResponseDto> searchTodo(String param, String username) {
//        return todoRepository.findByTitleContainingAndOpenTrueOrUserUsernameOrderByCreatedAtDesc(param, username)
//                .stream().map(TodoResponseDto::new).toList();
        return todoRepositoryQuery.findAllByUserName(username).stream().map(TodoResponseDto::new).toList();
    }
}
