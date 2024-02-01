package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.ResultResponseDto;
import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<Object> signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            ResultResponseDto responseDto = new ResultResponseDto("중복된 사용자가 존재합니다.", 400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            ResultResponseDto responseDto = new ResultResponseDto("중복된 Email 입니다.", 400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }

        // 사용자 등록
        User user = new User(username, password, email);
        userRepository.save(user);

        ResultResponseDto responseDto = new ResultResponseDto("회원가입에 성공하셨습니다", 200);
        return ResponseEntity.ok(responseDto);
    }
}
