package com.example.barogo.controller;

import com.example.barogo.domain.User;
import com.example.barogo.dto.LoginRequest;
import com.example.barogo.dto.LoginResponse;
import com.example.barogo.dto.UserRegisterRequest;
import com.example.barogo.exception.PasswordExpiredException;
import com.example.barogo.exception.UnauthorizedException;
import com.example.barogo.security.JwtProvider;
import com.example.barogo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
        User user = userService.findByUserId(loginRequest.getUserId());

//      if (user.isAccountLocked()) throw new UnauthorizedException("계정이 잠겨 있습니다.");

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            userService.increaseFailedCount(user); // 크리덴셜스터핑
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        if (user.isPasswordExpired()) {
            throw new PasswordExpiredException("비밀번호가 만료되었습니다.");
        }

        userService.resetFailedCount(user);

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userService.save(user);

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }
}
