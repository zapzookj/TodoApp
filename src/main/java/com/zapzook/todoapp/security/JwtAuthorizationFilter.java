package com.zapzook.todoapp.security;

import com.zapzook.todoapp.entity.RefreshToken;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.repository.RefreshTokenRepository;
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
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Util util;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, RefreshTokenRepository refreshTokenRepository, Util util) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.util = util;
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
                    UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
                    User user = userDetails.getUser();
                    RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId());

                    if (refreshToken != null && jwtUtil.validateToken(refreshToken.getToken()) == 0) {
                        String newToken = jwtUtil.createToken(username);
                        res.addHeader(JwtUtil.AUTHORIZATION_HEADER, newToken);
                        util.authResult(res, "해당 Access 토큰은 만료되었습니다. 새로운 토큰을 헤더에 발급합니다.", 200);
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
                setAuthentication(info.getSubject());
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
