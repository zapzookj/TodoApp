package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.TodoRepository;
import com.zapzook.todoapp.repository.TodoRepositoryQueryImpl;
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
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoRepositoryQueryImpl todoRepositoryQuery;
    private final Util util;
    @Transactional
    public void createTodo(TodoRequestDto requestDto, User user) {
        todoRepository.save(new Todo(requestDto, user));
    }

    public TodoResponseDto getTodo(Long todoId, String username) {
        Todo todo = util.findTodo(todoId);
        if(todo.getCompleted()){
            throw new IllegalArgumentException("해당 할일카드는 완료되어 숨김처리 되었습니다.");
        }
        if(!todo.getOpen() && !todo.getUser().getUsername().equals(username)){
            throw new IllegalArgumentException("비공개된 할일카드입니다. 작성자만 조회가 가능합니다.");
        }
        return new TodoResponseDto(todo);
    }

    public List<TodoResponseDto> getMyTodos(String username) {
        return todoRepositoryQuery.findAllWithUser(username).stream().map(TodoResponseDto::new).toList();
    }

    public Page<TodoResponseDto> getTodoList(String username, int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = setPageable(page, size, sortBy, isAsc);
        return todoRepositoryQuery.findAllByUserName(username, pageable).map(TodoResponseDto::new);
    }
    @Transactional
    public void updateTodo(Long todoId, TodoRequestDto requestDto, User user) {
        Todo todo = util.findTodo(todoId, user);
        todo.update(requestDto);
    }
    @Transactional
    public void completeTodo(Long todoId, User user) {
        Todo todo = util.findTodo(todoId, user);
        todo.complete();
    }

    public Page<TodoResponseDto> searchTodo(String param, String username, int page, int size, String sortBy, boolean isAsc) {
        Pageable pageable = setPageable(page, size, sortBy, isAsc);
        return todoRepositoryQuery.findAllByParamAndUserName(param, username, pageable).map(TodoResponseDto::new);
    }

    public Pageable setPageable(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
}
