package com.moonkeyeu.core.api.security.validators;

import com.moonkeyeu.core.api.user.reporitory.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public record ExistsByEmailValidator(UserRepository userRepository)
        implements ConstraintValidator<ExistsByEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userRepository.existsByEmail(email);
    }
}