package com.kameleoon.TrialTask.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LoginDto {
    @NotNull
    private String username;

    @NotNull
    private String password;

}
