package com.zapzook.todoapp.controller;

import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/todo/{todoId}")
    public TodoResponseDto getTodo(@PathVariable Long todoId){
        return todoService.getTodo(todoId);
    }

    @GetMapping("/todo")
    public List<TodoResponseDto> getTodoList(){
        return todoService.getTodoList();
    }

    @PostMapping("/todo")
    public TodoResponseDto createTodo(@RequestBody TodoRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
            return todoService.createTodo(requestDto, userDetails.getUser());
    }

    @PostMapping("/todo/{todoId}")
    public String completeTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return todoService.completeTodo(todoId, userDetails.getUser());
    }

    @PutMapping("/todo/{todoId}")
    public TodoResponseDto updateTodo(@PathVariable Long todoId, @RequestBody TodoRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        return todoService.updateTodo(todoId, requestDto, userDetails.getUser());
    }
}
