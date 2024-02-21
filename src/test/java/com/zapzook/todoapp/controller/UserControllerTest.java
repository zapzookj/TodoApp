package com.zapzook.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zapzook.todoapp.config.WebSecurityConfig;
import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.repository.RefreshTokenRepository;
import com.zapzook.todoapp.security.UserDetailsServiceImpl;
import com.zapzook.todoapp.service.UserService;
import com.zapzook.todoapp.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = {UserController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class UserControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthenticationConfiguration authenticationConfiguration;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signupSuccess() throws Exception {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("name", "password", "test@sparta.com");
        String signupRequestBody = objectMapper.writeValueAsString(signupRequestDto);

        // when - then
        mvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 - 실패(Validation 오류)")
    void signupFail() throws Exception {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("Name", "password!", "testsparta.com");
        String signupRequestBody = objectMapper.writeValueAsString(signupRequestDto);

        // when - then
        mvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestBody))
                .andExpect(status().isBadRequest());
    }

}