package com.zapzook.todoapp.controller;

import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.dto.ResultResponseDto;
import com.zapzook.todoapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/user/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<ResultResponseDto> ResultList = new ArrayList<>();
        ResultList.add(new ResultResponseDto("회원가입 실패!", 400));
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
                ResultList.add(new ResultResponseDto(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage(), 400));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultList);
        }
        userService.signup(requestDto);
        ResultResponseDto responseDto = new ResultResponseDto("회원가입에 성공하셨습니다", 200);
        return ResponseEntity.ok(responseDto);
    }
}
