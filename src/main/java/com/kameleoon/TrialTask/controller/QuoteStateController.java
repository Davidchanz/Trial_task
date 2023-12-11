package com.kameleoon.TrialTask.controller;

import com.kameleoon.TrialTask.dto.ApiResponse;
import com.kameleoon.TrialTask.dto.ApiResponseSingleOk;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.service.QuoteService;
import com.kameleoon.TrialTask.service.QuoteStateService;
import com.kameleoon.TrialTask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/vote")
public class QuoteStateController {
    @Autowired
    public QuoteService quoteService;

    @Autowired
    public UserService userService;

    @Autowired
    public QuoteStateService quoteStateService;

    @PostMapping("/up/{id}")
    public ResponseEntity<ApiResponse> upVote(@Valid @PathVariable Long id, Principal principal){
        User currentUser = userService.findUserByUserName(principal.getName());
        Quote quote = quoteService.findQuoteById(id);
        quoteStateService.addUpVoteQuoteState(quote, currentUser);
        return new ResponseEntity<>(new ApiResponseSingleOk("Up Vote", "Up Vote for Quote [" + quote.getText() + "] by user [" + currentUser.getUsername() + "]"), HttpStatus.OK);
    }

    @PostMapping("/down/{id}")
    public ResponseEntity<ApiResponse> downVote(@Valid @PathVariable Long id, Principal principal){
        User currentUser = userService.findUserByUserName(principal.getName());
        Quote quote = quoteService.findQuoteById(id);
        quoteStateService.addDownVoteQuoteState(quote, currentUser);
        return new ResponseEntity<>(new ApiResponseSingleOk("Down Vote", "Down Vote for Quote [" + quote.getText() + "] by user [" + currentUser.getUsername() + "]"), HttpStatus.OK);
    }
}
