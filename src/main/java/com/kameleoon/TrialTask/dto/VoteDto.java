package com.kameleoon.TrialTask.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class VoteDto {

    @NotNull
    private Instant votedOn;

    @NotNull
    private Integer voteValue;
}
