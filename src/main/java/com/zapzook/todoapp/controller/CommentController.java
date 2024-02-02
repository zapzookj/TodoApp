package com.zapzook.todoapp.controller;

import com.zapzook.todoapp.dto.CommentRequestDto;
import com.zapzook.todoapp.dto.CommentResponseDto;
import com.zapzook.todoapp.dto.ResultResponseDto;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
@Tag(name = "Comment", description = "댓글 CUD 기능 수행")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Post comment", description = "특정 할일카드에 댓글을 생성한다.")
    @PostMapping("{todoId}/comment")
    public CommentResponseDto createComment(@PathVariable Long todoId,
                                            @RequestBody CommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.createComment(todoId, requestDto, userDetails.getUser());
    }

    @Operation(summary = "Put comment", description = "특정 할일카드에 달린 특정 댓글을 수정한다")
    @PutMapping("{todoId}/comment/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long todoId,
                                            @PathVariable Long commentId,
                                            @RequestBody CommentRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.updateComment(todoId, commentId, requestDto, userDetails.getUser());
    }

    @Operation(summary = "Delete comment", description = "특정 할일카드에 달린 특정 댓글을 삭제한다.")
    @DeleteMapping("{todoId}/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long todoId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        commentService.deleteComment(todoId, commentId, userDetails.getUser());
        ResultResponseDto responseDto = new ResultResponseDto("삭제 성공.", HttpStatus.OK.value());
        return ResponseEntity.ok(responseDto);
    }
}
