package com.zapzook.todoapp.dto;

import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    public String contents;
    public Long todoId;
    public String username;
    public String todoWriter;

    public CommentResponseDto(Comment comment){
        this.contents = comment.getContents();
        this.todoId = comment.getTodo().getId();
        this.username = comment.getUser().getUsername();
        this.todoWriter = comment.getTodo().getUser().getUsername();
    }
}
