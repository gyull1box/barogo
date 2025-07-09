package com.example.barogo.service;

import com.example.barogo.domain.User;
import com.example.barogo.dto.UserRegisterRequest;
import com.example.barogo.repository.UserRepository;
import com.example.barogo.type.UserType;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public void register(UserRegisterRequest userRegisterRequest){
        String id = userRegisterRequest.getUserId().toLowerCase().trim();
        if(userRepository.existsByUserId(id)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        String encodedPw = passwordEncoder.encode(userRegisterRequest.getPassword());
        User user = new User();
        user.setUserId(id);
        user.setPassword(encodedPw);
        user.setName(userRegisterRequest.getName());
        user.setCreateUser(id);
        user.setCreateDate(new Timestamp(System.currentTimeMillis()));
        user.setPhone(userRegisterRequest.getPhone());
        user.setType(UserType.CUSTOMER.getCode()); // 회원가입을 통해 등록된 사용자는 일반 소비자로 분류한다는 가정

        userRepository.save(user);
    }
}
