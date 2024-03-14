package com.zapzook.todoapp.security;

import com.zapzook.todoapp.repository.RefreshTokenRedisRepository;
import com.zapzook.todoapp.util.JwtUtil;
import com.zapzook.todoapp.util.Util;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final Util util;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, Util util, RefreshTokenRedisRepository refreshTokenRedisRepository) {
        this.jwtUtil = jwtUtil;
        this.util = util;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = jwtUtil.getJwtFromHeader(req);

        if (StringUtils.hasText(tokenValue)) {
            int tokenStatus = jwtUtil.validateToken(tokenValue);

            if (tokenStatus == 1) { // 유효하지 않은 토큰
                util.authResult(res, "JWT 토큰이 유효하지 않습니다.", 400);
                return;
            } else if (tokenStatus == 2) { // 만료된 토큰
                try {
                    Claims info = jwtUtil.getExpiredTokenClaims(tokenValue);
                    String username = info.getSubject();
                    Long userId = info.get("userId", Long.class);
                    String email = info.get("email", String.class);
                    String profileImage = info.get("profileImage", String.class);
                    String introduce = info.get("introduce", String.class);
                    if(refreshTokenRedisRepository.existsByKey(username)) {
                        String newToken = jwtUtil.createToken(username, userId, email, profileImage, introduce);
                        res.addHeader(JwtUtil.AUTHORIZATION_HEADER, newToken);
                        util.authResult(res, "Access 토큰이 만료되었습니다. 새로운 토큰을 헤더에 발급합니다.", 200);
                        return;
                    } else {
                        util.authResult(res, "Access 토큰과 Refresh 토큰이 모두 만료되었습니다. 다시 로그인 해주세요.", 401);
                        return;
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return;
                }
            } else { // 유효한 토큰
                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
                setAuthentication(info.getSubject(), info.get("userId", Long.class),
                        info.get("email", String.class), info.get("profileImage", String.class), info.get("introduce", String.class));
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username, Long userId, String email, String profileImage, String introduce) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username, userId, email, profileImage, introduce);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username, Long userId, String email, String profileImage, String introduce) {
        UserDetails userDetails = new UserDetailsImpl(userId, username, email, profileImage, introduce);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
