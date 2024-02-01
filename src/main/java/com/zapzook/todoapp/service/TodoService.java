package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.TodoRepository;
import com.zapzook.todoapp.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto requestDto, User user) {
        Todo todo = todoRepository.save(new Todo(requestDto, user));
        return new TodoResponseDto(todo);
    }

    public TodoResponseDto getTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new IllegalArgumentException("해당 할일카드가 존재하지 않습니다.")
        );
        return new TodoResponseDto(todo);
    }

    public List<TodoResponseDto> getTodoList() {
        return todoRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(todo -> new TodoResponseDto(todo, 1)).toList();
    }
    @Transactional
    public TodoResponseDto updateTodo(Long todoId, TodoRequestDto requestDto, User user) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new IllegalArgumentException("해당 할일카드가 존재하지 않습니다.")
        );
        if(!user.getUsername().equals(todo.getUser().getUsername())){
            throw new IllegalArgumentException("username이 일치하지 않습니다!");
        }
        todo.update(requestDto);
        return new TodoResponseDto(todo);
    }
    @Transactional
    public String completeTodo(Long todoId, User user) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> new IllegalArgumentException("해당 할일카드가 존재하지 않습니다.")
        );
        if(!user.getUsername().equals(todo.getUser().getUsername())){
            throw new IllegalArgumentException("username이 일치하지 않습니다!");
        }
        todo.complete();
        return ("할일 카드 id : " + todo.getId() + "\n완료 여부 : True");
    }
}
