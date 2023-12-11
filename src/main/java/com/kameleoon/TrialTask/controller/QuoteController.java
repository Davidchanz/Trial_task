package com.kameleoon.TrialTask.controller;

import com.kameleoon.TrialTask.dto.*;
import com.kameleoon.TrialTask.exception.RequiredRequestParamIsMissingException;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.service.QuoteService;
import com.kameleoon.TrialTask.service.QuoteStateService;
import com.kameleoon.TrialTask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/quote")
public class QuoteController {

    @Autowired
    public QuoteService quoteService;

    @Autowired
    public UserService userService;

    @Autowired
    public QuoteStateService quoteStateService;

    @GetMapping("/{id}")
    public ResponseEntity<QuoteDto> getQuote(@Valid @PathVariable Long id){
        return new ResponseEntity<>(new QuoteDto(quoteService.findQuoteById(id)), HttpStatus.OK);
    }

    @GetMapping("/rnd")
    public ResponseEntity<QuoteDto> getRandomQuote(){
        return new ResponseEntity<>(new QuoteDto(quoteService.getRandomQuote()), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<QuoteDto> addNewQuote(
            @Valid @RequestBody(required = false) QuoteContentDto quoteContentDto,
            Principal principal){
        if(quoteContentDto == null)
            throw new RequiredRequestParamIsMissingException("Required request param QuoteContentDto is missing");

        Quote quote = new Quote(quoteContentDto);
        quote.setAuthor(userService.findUserByUserName(principal.getName()));
        quoteService.addNewQuote(quote);
        return new ResponseEntity<>(new QuoteDto(quote), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateQuote(
            @Valid @PathVariable Long id,
            @Valid @RequestBody(required = false) QuoteContentDto quoteContentDto,
            Principal principal){
        if(quoteContentDto == null)
            throw new RequiredRequestParamIsMissingException("Required request param QuoteContentDto is missing");

        User currentUser = userService.findUserByUserName(principal.getName());
        quoteService.updateQuote(currentUser, id, quoteContentDto);
        return new ResponseEntity<>(
                new ApiResponseSingleOk("Update Quote", "New Quote content is [" + quoteContentDto.getText() + "]"),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteQuote(@Valid @PathVariable Long id, Principal principal){
        User currentUser = userService.findUserByUserName(principal.getName());
        Quote quote = quoteService.deleteQuote(currentUser, id);
        return new ResponseEntity<>(
                new ApiResponseSingleOk("Delete Quote", "Quote [" + quote.getText() + "] was deleted!"),
                HttpStatus.OK);
    }

    @GetMapping("/top10")
    public ResponseEntity<List<QuoteDto>> getTop10Quotes(){
        var top10Quotes = quoteStateService.getTop10QuoteStates()
                .stream()
                .map(quoteStateDto ->
                        new QuoteDto(quoteStateDto.getQuote()))
                .toList();
        return new ResponseEntity<>(top10Quotes, HttpStatus.OK);
    }

    @GetMapping("/worse10")
    public ResponseEntity<List<QuoteDto>> getWorse10Quotes(){
        var top10Quotes = quoteStateService.getWorse10QuoteStates()
                .stream()
                .map(quoteStateDto ->
                        new QuoteDto(quoteStateDto.getQuote()))
                .toList();
        return new ResponseEntity<>(top10Quotes, HttpStatus.OK);
    }

    @GetMapping("/graph/{id}")
    public ResponseEntity<GraphDto> getQuoteGraph(@Valid @PathVariable Long id){
        Quote quote = quoteService.findQuoteById(id);
        var votes = quoteStateService.getQuoteVoteGraphData(quote);
        GraphDto graphDto = new GraphDto();
        List<VoteDto> data = new ArrayList<>();
        int rating = 0;
        data.add(new VoteDto(quote.getCreatedOn(), rating));
        for(var vote: votes){
            rating += vote.getVoteValue();
            data.add(new VoteDto(vote.getVotedOn(), rating));
        }
        graphDto.setData(data);
        graphDto.setMaxRating(data.get(data.size()-1).getVoteValue());
        graphDto.setMinRating(data.get(0).getVoteValue());
        graphDto.setMaxTime(data.get(data.size()-1).getVotedOn());
        graphDto.setMinTime(data.get(0).getVotedOn());
        return new ResponseEntity<>(graphDto, HttpStatus.OK);
    }
}
