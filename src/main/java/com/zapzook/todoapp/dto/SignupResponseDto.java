package com.zapzook.todoapp.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SignupResponseDto {
    private String message;
    private int statusCode;

    public SignupResponseDto(String message, int statusCode){
        this.message = message;
        this.statusCode = statusCode;
    }
}
