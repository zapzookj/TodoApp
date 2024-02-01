package com.zapzook.todoapp.dto;

import com.zapzook.todoapp.entity.Todo;
import com.zapzook.todoapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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

    public TodoResponseDto(Todo todo){
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.contents = todo.getContents();
        this.username = todo.getUser().getUsername();
        this.createdAt = todo.getCreatedAt();
    }
    public TodoResponseDto(Todo todo, int input){
        this.title = todo.getTitle();
        this.username = todo.getUser().getUsername();
        this.createdAt = todo.getCreatedAt();
        this.completed = todo.getCompleted();
    }
}
