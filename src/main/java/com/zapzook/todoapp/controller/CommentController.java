package com.zapzook.todoapp.controller;

import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.dto.SuccessResponseDto;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("{todoId}/comment")
    public CommentResponseDto createComment(@PathVariable Long todoId,
                                            @RequestBody CommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.createComment(todoId, requestDto, userDetails.getUser());
    }

    @PutMapping("{todoId}/comment/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long todoId,
                                            @PathVariable Long commentId,
                                            @RequestBody CommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.updateComment(todoId, commentId, requestDto, userDetails.getUser());
    }

    @DeleteMapping("{todoId}/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long todoId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        commentService.deleteComment(todoId, commentId, userDetails.getUser());
        SuccessResponseDto responseDto = new SuccessResponseDto("삭제 성공.", HttpStatus.OK.value());
        return ResponseEntity.ok(responseDto);
    }
}
