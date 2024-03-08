package com.zapzook.todoapp.controller;

import com.zapzook.todoapp.dto.ResultResponseDto;
import com.zapzook.todoapp.dto.TodoRequestDto;
import com.zapzook.todoapp.dto.TodoResponseDto;
import com.zapzook.todoapp.exception.NotFoundException;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TodoResponseDto> getTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws NotFoundException {
        TodoResponseDto todoResponseDto = todoService.getTodo(todoId, userDetails.getUser().getUsername());
        return ResponseEntity.status(200).body(todoResponseDto);
    }

    @Operation(summary = "Get searched todo", description = "RequestParam 형식으로 제목에 특정 키워드(param)가 포함된 할일카드들을 조회한다.")
    @GetMapping("/todos/search")
    public ResponseEntity<List<TodoResponseDto>> searchTodo(@RequestParam String param, @AuthenticationPrincipal UserDetailsImpl userDetails){
        List<TodoResponseDto> todoResponseDtoList = todoService.searchTodo(param, userDetails.getUser().getUsername());
        return ResponseEntity.status(200).body(todoResponseDtoList);
    }

    @Operation(summary = "Get todoList", description = "모든 할일카드들을 조회한다.")
    @GetMapping("/todos")
    public ResponseEntity<List<TodoResponseDto>> getTodoList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<TodoResponseDto> todoResponseDtoList = todoService.getTodoList(userDetails.getUser().getUsername());
        return ResponseEntity.status(200).body(todoResponseDtoList);
    }

    @Operation(summary = "Post todo", description = "새로운 할일카드를 생성한다.")
    @PostMapping("/todo")
    public ResponseEntity<ResultResponseDto> createTodo(@RequestBody TodoRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        todoService.createTodo(requestDto, userDetails.getUser());
        return ResponseEntity.status(200).body(new ResultResponseDto("할일 카드 생성 성공", 200));
    }

    @Operation(summary = "Post select todo", description = "특정 할일카드를 완료처리한다(작성자만 가능).")
    @PostMapping("/todo/complete/{todoId}")
    public ResponseEntity<ResultResponseDto> completeTodo(@PathVariable Long todoId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws NotFoundException {
        todoService.completeTodo(todoId, userDetails.getUser());
        return ResponseEntity.status(200).body(new ResultResponseDto("할일 카드 완료 처리 성공", 200));
    }

    @Operation(summary = "Put select todo", description = "특정 할일카드의 내용을 수정한다(작성자만 가능).")
    @PutMapping("/todo/{todoId}")
    public ResponseEntity<ResultResponseDto> updateTodo(@PathVariable Long todoId, @RequestBody TodoRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws NotFoundException {
        todoService.updateTodo(todoId, requestDto, userDetails.getUser());
        return ResponseEntity.status(200).body(new ResultResponseDto("할일 카드 수정 성공", 200));
    }
}
