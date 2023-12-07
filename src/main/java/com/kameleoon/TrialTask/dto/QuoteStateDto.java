package com.kameleoon.TrialTask.dto;

import com.kameleoon.TrialTask.model.Quote;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class QuoteStateDto {

    @NotNull
    private Quote quote;

    @NotNull
    private Long rating;
}
