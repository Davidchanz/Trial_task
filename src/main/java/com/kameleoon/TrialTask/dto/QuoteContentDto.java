package com.kameleoon.TrialTask.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class QuoteContentDto {

    @NotNull(message = "Quote text must not be empty!")
    @Size(min = 10, max = 128, message = "Quote size must be between 10 and 128!")
    private String text;
}
