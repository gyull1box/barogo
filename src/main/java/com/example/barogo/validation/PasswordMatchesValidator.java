package com.example.barogo.validation;

import com.example.barogo.dto.UserRegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegisterRequest> {

    @Override
    public boolean isValid(UserRegisterRequest dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true;
        String pw  = dto.getPassword();
        String pw2 = dto.getPasswordConfirm();
        return pw != null && pw.equals(pw2);
    }
}
