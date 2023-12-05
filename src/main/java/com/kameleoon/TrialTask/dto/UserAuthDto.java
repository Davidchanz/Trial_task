package com.kameleoon.TrialTask.dto;

import com.kameleoon.TrialTask.validation.annotation.PasswordMatches;
import com.kameleoon.TrialTask.validation.annotation.ValidEmail;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@PasswordMatches
public class UserAuthDto {
    @NotNull(message = "Username must not be null!")
    @Size(min = 5, max = 25, message = "Username must be between 5 and 25!")
    private String username;

    @NotNull(message = "Password must not be null!")
    @Size(min = 8, max = 25, message = "Password must be between 8 and 25!")
    private String password;

    @NotNull(message = "Confirm Password must not be null!")
    private String matchingPassword;

    @ValidEmail
    @NotNull(message = "Email must not be null!")
    private String email;
}
