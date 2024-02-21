package com.zapzook.todoapp.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class MockSpringSecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        SecurityContextHolder.getContext()
                .setAuthentication((Authentication) ((HttpServletRequest) req).getUserPrincipal());
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        SecurityContextHolder.clearContext();
    }
//    public void getFilters(MockHttpServletRequest mockHttpServletRequest){} // todoController의 할일 완료 처리 테스트가 제대로 동작한다
}