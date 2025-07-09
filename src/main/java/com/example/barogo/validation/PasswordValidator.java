package com.example.barogo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("비밀번호를 입력해주세요.")
                    .addConstraintViolation();
            return false;
        }

        if (password.length() < 12) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("비밀번호는 12자 이상이어야 합니다.")
                    .addConstraintViolation();
            return false;
        }

        int count = 0;
        if (password.matches(".*[a-z].*")) count++;
        if (password.matches(".*[A-Z].*")) count++;
        if (password.matches(".*\\d.*")) count++;
        if (password.matches(".*[^a-zA-Z0-9].*")) count++;

        if (count < 3) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("비밀번호는 대/소문자/숫자/특수문자 중 3종류 이상 포함해야 합니다.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
