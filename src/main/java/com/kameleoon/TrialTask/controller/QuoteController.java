package com.kameleoon.TrialTask.controller;

import com.kameleoon.TrialTask.dto.QuoteDto;
import com.kameleoon.TrialTask.dto.UserDto;
import com.kameleoon.TrialTask.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class QuoteController {

    @Autowired
    public QuoteService quoteService;

    @GetMapping("/quote/{id}")
    public ResponseEntity<QuoteDto> getQuote(@Valid @PathVariable Long id){
        return new ResponseEntity<>(new QuoteDto(quoteService.findQuoteById(id)), HttpStatus.OK);
    }

    //todo get random Quote
    //todo add new Quote
    //todo update Quote
    //todo delete Quote from DB
    //todo set Quote flag archive true

    //todo upVote
    //todo downVote

    //todo get top 10 Quotes
    //todo get worse 10 Quotes
    //todo get Quote stats graph
}
