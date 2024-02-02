package com.zapzook.todoapp.controller;

import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Todo", description = "할일카드(Todo)의 CRU 기능 수행, 숨김처리된 할일카드는 조회되지 않으며 비공개 처리된 할일카드는 작성자만 조회가 가능하다.")
public class TodoController {

    private final TodoService todoService;
    @Operation(summary = "Get select todo", description = "특정 할일카드와 해당 할일카드에 달린 댓글들을 조회한다.")
    @GetMapping("/todo/{todoId}")
    public TodoResponseDto getTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return todoService.getTodo(todoId, userDetails.getUser().getUsername());
    }

    @Operation(summary = "Get searched todo", description = "RequestParam 형식으로 제목에 특정 키워드(param)가 포함된 할일카드들을 조회한다.")
    @GetMapping("/todo/search")
    public List<TodoResponseDto> searchTodo(@RequestParam String param, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return todoService.searchTodo(param, userDetails.getUser().getUsername());
    }

    @Operation(summary = "Get todoList", description = "모든 할일카드들을 조회한다.")
    @GetMapping("/todo")
    public List<TodoResponseDto> getTodoList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return todoService.getTodoList(userDetails.getUser().getUsername());
    }

    @Operation(summary = "Post todo", description = "새로운 할일카드를 생성한다.")
    @PostMapping("/todo")
    public TodoResponseDto createTodo(@RequestBody TodoRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
            return todoService.createTodo(requestDto, userDetails.getUser());
    }

    @Operation(summary = "Post select todo", description = "특정 할일카드를 완료처리한다(작성자만 가능).")
    @PostMapping("/todo/{todoId}")
    public String completeTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return todoService.completeTodo(todoId, userDetails.getUser());
    }

    @Operation(summary = "Put select todo", description = "특정 할일카드의 내용을 수정한다(작성자만 가능).")
    @PutMapping("/todo/{todoId}")
    public TodoResponseDto updateTodo(@PathVariable Long todoId, @RequestBody TodoRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        return todoService.updateTodo(todoId, requestDto, userDetails.getUser());
    }
}
