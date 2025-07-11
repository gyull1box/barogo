package com.example.barogo.controller;

import com.example.barogo.domain.User;
import com.example.barogo.dto.*;
import com.example.barogo.exception.PasswordExpiredException;
import com.example.barogo.exception.UnauthorizedException;
import com.example.barogo.security.JwtAuthenticationFilter;
import com.example.barogo.security.JwtProvider;
import com.example.barogo.service.UserService;
import com.example.barogo.type.OrderStatusType;
import com.example.barogo.type.UserType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserService userService,
                          BCryptPasswordEncoder passwordEncoder,
                          JwtProvider jwtProvider){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        userService.register(userRegisterRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.authenticate(loginRequest.getUserId(), loginRequest.getPassword());

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userService.save(user);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(accessToken, refreshToken));
    }


    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getUserOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) OrderStatusType status,
            Principal principal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int limit) {

        if (startDate == null || endDate == null || ChronoUnit.DAYS.between(startDate, endDate) > 2) {
            throw new IllegalArgumentException("조회 범위는 최대 3일입니다.");
        }

        if(startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("조회 시작일이 종료일보다 클 수 없습니다.");
        }

        String userId = principal.getName();
        Date fromDate = java.sql.Date.valueOf(startDate);
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        Date toDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Page<OrderDto> orders = userService.searchOrders(userId, fromDate, toDate, status, page, limit);
        return ResponseEntity.ok(ApiResponse.success(orders.getContent()));
    }


    @PatchMapping("/orders/{orderId}/modify")
    public ResponseEntity<ApiResponse<OrderDto>> modifyOrder(
            @PathVariable Long orderId,
            @RequestBody OrderModifyRequest request,
            Principal principal) {

        String userId = principal.getName();
        OrderDto modifiedOrder = userService.modifyOrder(userId, orderId, request);
        return ResponseEntity.ok(ApiResponse.success(modifiedOrder));
    }
}
