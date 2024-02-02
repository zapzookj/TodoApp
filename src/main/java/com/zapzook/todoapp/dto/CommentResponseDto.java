package com.zapzook.todoapp.dto;

import com.zapzook.todoapp.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    public Long commentId;
    public String contents;
    public String commentWriter;

    public CommentResponseDto(Comment comment){
        this.commentId = comment.getId();
        this.contents = comment.getContents();
        this.commentWriter = comment.getUser().getUsername();
    }
}
