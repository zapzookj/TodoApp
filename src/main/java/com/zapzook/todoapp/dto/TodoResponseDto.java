package com.zapzook.todoapp.dto;

import com.zapzook.todoapp.entity.Comment;
import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {
    private Long id;
    private String title;
    private String contents;
    private String username;
    private Date createdAt;
    private Boolean completed;
    private List<CommentResponseDto> commentList;

    public TodoResponseDto(Todo todo){
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.contents = todo.getContents();
        this.username = todo.getUser().getUsername();
        this.createdAt = todo.getCreatedAt();
        this.completed = todo.getCompleted();
    }
    public TodoResponseDto(Todo todo, List<CommentResponseDto> commentList){
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.contents = todo.getContents();
        this.username = todo.getUser().getUsername();
        this.createdAt = todo.getCreatedAt();
        this.completed = todo.getCompleted();
        this.commentList = commentList;
    }
}
