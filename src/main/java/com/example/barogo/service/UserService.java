package com.example.barogo.service;

import com.example.barogo.domain.User;
import com.example.barogo.dto.UserRegisterRequest;
import com.example.barogo.repository.UserRepository;
import com.example.barogo.type.UserType;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final static int FAIL_PERMIT_COUNT = 5;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public void register(UserRegisterRequest userRegisterRequest){
        // 연락처를 비롯한 개인정보 인증 후 이미 가입된 회원인지 확인필요
        String id = userRegisterRequest.getUserId().toLowerCase().trim();
        if(userRepository.existsByUserId(id)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setName(userRegisterRequest.getName());
        user.setCreateUser(id);
        user.setUserId(id);

        LocalDateTime now = LocalDateTime.now();

        user.setCreateDate(now);
        user.setPhone(userRegisterRequest.getPhone());
        user.setPasswordExpireDate(now.plusDays(90).toLocalDate());
        user.setStartUseDttm(now);
        user.setType(UserType.CUSTOMER.getCode()); // 회원가입을 통해 등록된 사용자는 일반 소비자로 분류한다는 가정

        userRepository.save(user);
    }

    @Transactional
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId.toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
    }

    @Transactional
    public void increaseFailedCount(User user) {
        int newCnt = user.getFailedCount() + 1;
        user.setFailedCount(newCnt);

        if (newCnt >= FAIL_PERMIT_COUNT) {
            // 본인인증 다시 -> 재설정 필요
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetFailedCount(User user){
        user.setFailedCount(0);
        userRepository.save(user);
    }

    @Transactional
    public void save(User user){
        userRepository.save(user);
    }
}
