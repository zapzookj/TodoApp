package com.zapzook.todoapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zapzook.todoapp.dto.LoginRequestDto;
import com.zapzook.todoapp.entity.RefreshToken;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.RefreshTokenRepository;
import com.zapzook.todoapp.util.JwtUtil;
import com.zapzook.todoapp.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final Util util;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, Util util, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.util = util;
        this.refreshTokenRepository = refreshTokenRepository;
        setFilterProcessesUrl("/api/user/login");
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException{
        UserDetailsImpl userDetails = ((UserDetailsImpl) authResult.getPrincipal());
        String username = userDetails.getUsername();
        String token = jwtUtil.createToken(username);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

//        String refreshToken = jwtUtil.createRefreshToken(username);
//        User user = userDetails.getUser();
//        refreshTokenRepository.save(new RefreshToken(refreshToken, user));

        util.authResult(response, "로그인 성공! Header에 JWT 토큰을 반환합니다.", 200);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException{
        util.authResult(response, "로그인 실패! username과 password를 다시 확인해보세요.", 401);
    }
}
