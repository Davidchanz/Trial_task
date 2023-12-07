package com.kameleoon.TrialTask.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@ToString
public class GraphDto {
    @NotNull
    private List<VoteDto> data;

    @NotNull
    private Integer minRating;

    @NotNull
    private Integer maxRating;

    @NotNull
    private Instant minTime;

    @NotNull
    private Instant maxTime;
}
