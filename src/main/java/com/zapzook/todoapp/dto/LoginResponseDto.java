package com.zapzook.todoapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String message;
    private String token;

    public LoginResponseDto(String message, String token){
        this.message = message;
        this.token = token;
    }
}
