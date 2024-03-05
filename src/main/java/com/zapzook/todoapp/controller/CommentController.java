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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Comment", description = "댓글 CUD 기능 수행")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Get comment", description = "특정 할일카드에 달린 댓글을 조회한다.")
    @GetMapping("/todo/{todoId}/comment")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long todoId) {
        List<CommentResponseDto> commentResponseDtoList = commentService.getComments(todoId);
        return ResponseEntity.status(200).body(commentResponseDtoList);
    }

    @Operation(summary = "Post comment", description = "특정 할일카드에 댓글을 생성한다.")
    @PostMapping("/todo/{todoId}/comment")
    public ResponseEntity<ResultResponseDto> createComment(@PathVariable Long todoId,
                                                           @RequestBody CommentRequestDto requestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.createComment(todoId, requestDto, userDetails.getUser());
        return ResponseEntity.status(200).body(new ResultResponseDto("댓글 작성 성공", 200));
    }

    @Operation(summary = "Put comment", description = "특정 할일카드에 달린 특정 댓글을 수정한다")
    @PutMapping("/todo/{todoId}/comment/{commentId}")
    public ResponseEntity<ResultResponseDto> updateComment(@PathVariable Long todoId,
                                                           @PathVariable Long commentId,
                                                           @RequestBody CommentRequestDto requestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.updateComment(todoId, commentId, requestDto, userDetails.getUser());
        return ResponseEntity.status(200).body(new ResultResponseDto("댓글 수정 성공", 200));
    }

    @Operation(summary = "Delete comment", description = "특정 할일카드에 달린 특정 댓글을 삭제한다.")
    @DeleteMapping("/todo/{todoId}/comment/{commentId}")
    public ResponseEntity<ResultResponseDto> deleteComment(@PathVariable Long todoId,
                                                           @PathVariable Long commentId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(todoId, commentId, userDetails.getUser());
        return ResponseEntity.status(200).body(new ResultResponseDto("댓글 삭제 성공", 200));
    }
}
