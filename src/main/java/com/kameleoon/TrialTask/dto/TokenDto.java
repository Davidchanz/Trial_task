package com.kameleoon.TrialTask.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {
    @NotNull
    private String token;
}
