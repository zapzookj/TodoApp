package com.zapzook.todoapp.controller;

import com.zapzook.todoapp.dto.ResultResponseDto;
import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "User", description = "사용자의 회원가입 기능 수행")
public class UserController {

    private final UserService userService;
    @Operation(summary = "Post user profile", description = "username, password, email을 입력해 회원가입을 한다.")
    @PostMapping("/user/signup")
    public ResponseEntity<ResultResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(200).body(new ResultResponseDto("회원가입 성공", 200));
    }
}
