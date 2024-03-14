package com.zapzook.todoapp.service;

import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.dto.UserRequestDto;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.UserRepository;
import com.zapzook.todoapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Client s3Client;
    private final JwtUtil jwtUtil;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 등록
        User user = new User(username, password, email);
        userRepository.save(user);
    }
    @Transactional
    public String setProfile(User user, UserRequestDto requestDto) throws IOException {
        String oldProfileImage = URLDecoder.decode(user.getProfileImage(), StandardCharsets.UTF_8);
        MultipartFile newProfileImage = requestDto.getProfileImage();

        if (oldProfileImage != null && !oldProfileImage.isEmpty()) {
            // 기존 프로필 이미지가 존재한다면 버킷에서 제거
            String oldProfileImageKey = extractKeyFromUrl(oldProfileImage);
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(oldProfileImageKey).build());
        }
        String profileImageKey = "profile-images/" + user.getId() + "-" + newProfileImage.getOriginalFilename();
        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(profileImageKey).build(), RequestBody.fromBytes(newProfileImage.getBytes()));

        GetUrlRequest profileImageUrl = GetUrlRequest.builder().bucket(bucket).key(profileImageKey).build();
        String profileImage = s3Client.utilities().getUrl(profileImageUrl).toExternalForm();

        userRepository.updateProfile(user.getId(), requestDto.getIntroduce(), profileImage);

        String newToken = jwtUtil.createToken(user.getUsername(), user.getId(), user.getEmail(), profileImage, requestDto.getIntroduce());
        return newToken;
    }

    private String extractKeyFromUrl(String imageUrl) {
        String key = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        return "profile-images/" + key;
    }
}
