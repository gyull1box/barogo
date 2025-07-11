package com.example.barogo;

import com.example.barogo.domain.OrderEntity;
import com.example.barogo.domain.User;
import com.example.barogo.dto.LoginRequest;
import com.example.barogo.dto.OrderDto;
import com.example.barogo.dto.OrderModifyRequest;
import com.example.barogo.dto.UserRegisterRequest;
import com.example.barogo.repository.OrderRepository;
import com.example.barogo.repository.UserRepository;
import com.example.barogo.service.UserService;
import com.example.barogo.type.OrderStatusType;
import com.example.barogo.type.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private UserService userService;
    @Autowired
    private OrderRepository orderRepository;

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

    @Test
    @WithMockUser(username = "sohee.jeon")
    void getUserOrders_success() throws Exception {
        String userId = "sohee.jeon";

        Page<OrderDto> dummyPage = new PageImpl<>(
                new ArrayList<>(List.of(
                        new OrderDto(userId, 1L, "ORD01", new Date(System.currentTimeMillis()), OrderStatusType.CONFIRM.getStatus())
                ))
        );

        Mockito.when(userService.searchOrders(
                Mockito.anyString(),
                Mockito.any(Date.class),
                Mockito.any(Date.class),
                Mockito.isNull(),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(dummyPage);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("sohee.jeon")
                .password("password")
                .roles(UserType.CUSTOMER.name())
                .build();

        Principal principal = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        mockMvc.perform(get("/api/user/orders")
                        .param("startDate", "2025-07-01")
                        .param("endDate", "2025-07-02")
                        .param("page", "1")
                        .param("limit", "10")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "sohee.jeon")
    void getUserOrders_fail() throws Exception {
        String userId = "sohee.jeon";

        Page<OrderDto> dummyPage = new PageImpl<>(
                new ArrayList<>(List.of(
                        new OrderDto(userId, 1L, "ORD01", new Date(System.currentTimeMillis()), OrderStatusType.CONFIRM.getStatus())
                ))
        );

        Mockito.when(userService.searchOrders(
                Mockito.anyString(),
                Mockito.any(Date.class),
                Mockito.any(Date.class),
                Mockito.isNull(),
                Mockito.anyInt(),
                Mockito.anyInt()
        )).thenReturn(dummyPage);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("sohee.jeon")
                .password("password")
                .roles(UserType.CUSTOMER.name())
                .build();

        Principal principal = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        mockMvc.perform(get("/api/user/orders")
                        .param("startDate", "2025-07-01")
                        .param("endDate", "2025-07-10")
                        .param("page", "1")
                        .param("limit", "10")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "sohee.jeon")
    void modifyOrder_success() throws Exception {
        Long orderId = 1L;
        String userId = "sohee.jeon";

        OrderModifyRequest request = new OrderModifyRequest();
        request.setZipCode("12345");
        request.setCity("Seoul");
        request.setDistrict("Gangnam");
        request.setDetail("123 Street");
        request.setPhone("010-1234-5678");
        request.setRecipientName("Sohee");
        request.setAddressName("Home");
        request.setMemo("변경된 메모");

        OrderDto modifiedOrderDto = new OrderDto(
                userId, orderId, "ORD01", new Date(), OrderStatusType.READY_TO_DELIVERY.getStatus()
        );

        Mockito.when(userService.modifyOrder(Mockito.eq(userId), Mockito.eq(orderId), Mockito.any(OrderModifyRequest.class)))
                .thenReturn(modifiedOrderDto);

        mockMvc.perform(patch("/api/user/orders/{orderId}/modify", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.userId").value(userId));
    }

    @Test
    @WithMockUser(username = "sohee.jeon")
    void modifyOrder_fail_whenOrderIsConfirmed() throws Exception {
//        Long orderId = 1L;
//        String userId = "sohee.jeon";
//
//        OrderModifyRequest request = new OrderModifyRequest();
//        request.setMemo("변경 메모");
//
//        OrderEntity confirmedOrder = new OrderEntity();
//        confirmedOrder.setOrderId(orderId);
//        confirmedOrder.setUserId(new User(userId));
//        confirmedOrder.setOrderStatus(OrderStatusType.CONFIRM.getStatus());
//
//        Mockito.when(orderRepository.findById(orderId))
//                .thenReturn(Optional.of(confirmedOrder));
//
//        Mockito.when(userService.modifyOrder(Mockito.eq(userId), Mockito.eq(orderId), Mockito.any()))
//                .thenThrow(new IllegalStateException("확정된 주문은 수정할 수 없습니다."));
//
//        mockMvc.perform(patch("/api/user/orders/{orderId}/modify", orderId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("확정된 주문은 수정할 수 없습니다."));
    }

}
