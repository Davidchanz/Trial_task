package com.kameleoon.TrialTask.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDto {

    @NotNull
    private String error;

    @NotNull
    private String description;
}
