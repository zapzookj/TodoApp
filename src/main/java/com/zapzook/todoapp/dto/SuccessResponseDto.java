package com.zapzook.todoapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponseDto {
    private String message;
    private int statusCode;

    public SuccessResponseDto(String message, int statusCode){
        this.message = message;
        this.statusCode = statusCode;
    }
}
