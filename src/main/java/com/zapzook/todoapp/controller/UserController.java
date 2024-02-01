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
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.ok("회원가입 실패!");
        }
        userService.signup(requestDto);
        ResultResponseDto responseDto = new ResultResponseDto("회원가입에 성공하셨습니다.", HttpStatus.OK.value());
        return ResponseEntity.ok(responseDto);
    }
//    @PostMapping("/user/signup")
//    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            // 회원가입 실패 응답 생성
//            String errorMsg = bindingResult.getFieldErrors().stream()
//                    .map(FieldError::getDefaultMessage)
//                    .findFirst()
//                    .orElse("Validation error");
//            SignupResponseDto responseDto = new SignupResponseDto(errorMsg, HttpStatus.BAD_REQUEST.value());
//            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
//        }
//
//        userService.signup(requestDto);
//        // 회원가입 성공 응답 생성
//        SignupResponseDto responseDto = new SignupResponseDto("회원가입에 성공하셨습니다.", HttpStatus.OK.value());
//        return ResponseEntity.ok(responseDto);
//    }


}
