package com.kameleoon.TrialTask.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiErrorDto {

    @NotNull
    private String title;

    @NotNull
    private int status;

    @NotNull
    private String instance;

    @NotNull
    private Instant created = Instant.now();

    @NotNull
    private List<ErrorDto> errors = new ArrayList<>();

    public ApiErrorDto(HttpStatus httpStatus, String instance, ErrorDto... errors){
        this.title = httpStatus.name();
        this.status = httpStatus.value();
        this.instance = instance;
        this.errors = List.of(errors);
    }

    public ApiErrorDto(HttpStatus httpStatus, String instance, String error, String description){
        this.title = httpStatus.name();
        this.status = httpStatus.value();
        this.instance = instance;
        this.errors = List.of(new ErrorDto(error, description));
    }
}
