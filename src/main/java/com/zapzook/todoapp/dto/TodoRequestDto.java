package com.zapzook.todoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequestDto {
    private String title;
    private String contents;
    private Boolean open;
}
