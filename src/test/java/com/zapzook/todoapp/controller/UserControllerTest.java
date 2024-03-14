package com.zapzook.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zapzook.todoapp.config.WebSecurityConfig;
import com.zapzook.todoapp.dto.SignupRequestDto;
import com.zapzook.todoapp.dto.UserRequestDto;
import com.zapzook.todoapp.entity.User;
import com.zapzook.todoapp.security.UserDetailsImpl;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
@ActiveProfiles("test")
class UserControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new MockSpringSecurityFilter())
                .build();
    }

    private void mockUserSetup() {
        String username = "test";
        String password = "password";
        String email = "email@email.com";
        User testUser = new User(username, password, email);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signupSuccessTest() throws Exception {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("name", "password", "test@sparta.com");
        String signupRequestBody = objectMapper.writeValueAsString(signupRequestDto);

        // when - then
        mvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입 성공"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("회원가입 - 실패(Validation 오류)")
    void signupFailTest() throws Exception {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("Name", "password!", "testsparta.com");
        String signupRequestBody = objectMapper.writeValueAsString(signupRequestDto);

        // when - then
        mvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("프로필 등록")
    @WithMockUser(username = "test")
    void setProfileTest() throws Exception {
        // given
        mockUserSetup();
        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "profile.jpg", "image/jpeg", "image data".getBytes());

        UserRequestDto requestDto = new UserRequestDto(profileImage, "test");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/api/user/profile")
                .file(profileImage)
                        .param("introduce", requestDto.getIntroduce())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .principal(mockPrincipal);
        // when - then
        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("유저 프로필 등록(수정) 성공, 새로운 토큰이 발급되었습니다."))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

}