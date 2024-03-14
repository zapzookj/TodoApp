package com.zapzook.todoapp.controller;

import com.zapzook.todoapp.dto.ResultResponseDto;
import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.dto.UserRequestDto;
import com.zapzook.todoapp.security.UserDetailsImpl;
import com.zapzook.todoapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "User", description = "사용자의 회원가입 기능 수행")
public class UserController {

    private final UserService userService;
    @Operation(summary = "Post signup info", description = "username, password, email을 입력해 회원가입을 한다.")
    @PostMapping("/user/signup")
    public ResponseEntity<ResultResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(200).body(new ResultResponseDto("회원가입 성공", 200));
    }

    @Operation(summary = "Post user profile", description = "유저의 프로필을 등록 및 수정한다.")
    @PostMapping("/user/profile")
    public ResponseEntity<ResultResponseDto> setProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @ModelAttribute UserRequestDto requestDto,
                                                        HttpServletResponse response) throws IOException {
        String newToken = userService.setProfile(userDetails.getUser(), requestDto);
        response.addHeader("Authorization", newToken);
        return ResponseEntity.status(200).body(new ResultResponseDto("유저 프로필 등록(수정) 성공, 새로운 토큰이 발급되었습니다.", 200));
    }
}
