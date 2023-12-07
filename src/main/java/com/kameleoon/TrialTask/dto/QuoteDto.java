package com.kameleoon.TrialTask.dto;

import com.kameleoon.TrialTask.model.Quote;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Setter
@Getter
@ToString
public class QuoteDto {
    @NotNull
    private Long id;

    @NotNull
    private String text;

    @NotNull
    private UserDto author;

    @NotNull
    private Instant createdOn;

    @NotNull
    private Instant lastUpdatedOn;

    public QuoteDto(Quote quote){
        this.id = quote.getId();
        this.text = quote.getText();
        this.author = new UserDto(quote.getAuthor());
        this.createdOn = quote.getCreatedOn();
        this.lastUpdatedOn = quote.getLastUpdatedOn();
    }
}
