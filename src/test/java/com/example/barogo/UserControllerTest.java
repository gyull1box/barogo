package com.example.barogo;

import com.example.barogo.domain.User;
import com.example.barogo.dto.LoginRequest;
import com.example.barogo.dto.UserRegisterRequest;
import com.example.barogo.repository.UserRepository;
import com.example.barogo.type.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId("sohee.jeon");
        user.setPassword(encoder.encode("Password123!"));
        user.setName("테스트");
        user.setType(UserType.CUSTOMER.getCode());  // enum 대신 문자열 직접 입력
        user.setPhone("01011112222");
        user.setPasswordExpireDate(LocalDate.now().plusDays(90));
        user.setStartUseDttm(LocalDateTime.now());
        user.setCreateDate(LocalDateTime.now());
        user.setCreateUser("tester");
        user.setUseYn('Y');

        userRepository.save(user);
    }

    @Test
    void register_success() throws Exception {
        UserRegisterRequest request = buildRequest("successUser", "Password123!", "테스트성공", "01011112222");
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void register_Fail_Name() throws Exception {
        UserRegisterRequest request = buildRequest("FailUser", "Password123!", "테스트_특수문자", "01011112222");
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserId("sohee.jeon");
        request.setPassword("Password123!");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }

    @Test
    void login_fail() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUserId("sohee.jeon");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }


    private UserRegisterRequest buildRequest(String userId, String password, String name, String phone) {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUserId(userId);
        request.setPassword(password);
        request.setPasswordConfirm(password);
        request.setName(name);
        request.setPhone(phone);
        return request;
    }

}
