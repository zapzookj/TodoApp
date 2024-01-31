package com.zapzook.todoapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TodoController {
    @GetMapping("/todo")
    public void sayHello(){
        System.out.println("로그인에 성공하셨군요.");
    }
}
