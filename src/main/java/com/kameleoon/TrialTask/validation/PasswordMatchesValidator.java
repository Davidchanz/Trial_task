package com.kameleoon.TrialTask.validation;

import com.kameleoon.TrialTask.dto.UserAuthDto;
import com.kameleoon.TrialTask.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserAuthDto> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserAuthDto user, ConstraintValidatorContext constraintValidatorContext) {
        return user.getPassword().equals(user.getMatchingPassword());
    }
}
