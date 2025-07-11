package com.example.barogo.dto;

import com.example.barogo.validation.PasswordMatches;
import com.example.barogo.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatches
public class UserRegisterRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String userId;

    @ValidPassword
    private String password;

    @NotBlank(message = "비밀번호 확인을 위해 한번 더 입력해주세요.")
    private String passwordConfirm;

    @NotBlank(message = "핸드폰번호를 입력해주세요.")
    @Pattern(regexp = "^[0-9]+$", message = "숫자만 입력 가능합니다.")
    private String phone;

    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름은 한글 또는 영문만 입력 가능합니다.")
    private String name;
}
