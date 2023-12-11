package com.kameleoon.TrialTask.controller;

import com.kameleoon.TrialTask.config.SecurityConfig;
import com.kameleoon.TrialTask.config.TestConfig;
import com.kameleoon.TrialTask.mapper.QuoteMapper;
import com.kameleoon.TrialTask.mapper.QuoteMapperImpl;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.repository.QuoteRepository;
import com.kameleoon.TrialTask.repository.QuoteStateRepository;
import com.kameleoon.TrialTask.service.QuoteService;
import com.kameleoon.TrialTask.service.QuoteStateService;
import com.kameleoon.TrialTask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ QuoteController.class })
@Import({ SecurityConfig.class, TestConfig.class })
public class QuoteStateControllerTest extends AbstractTest{

    @MockBean
    QuoteService quoteService;

    @MockBean
    UserService userService;

    @MockBean
    QuoteStateService quoteStateService;

    @MockBean
    QuoteStateRepository quoteStateRepository;

    @MockBean
    QuoteRepository quoteRepository;

    @InjectMocks
    QuoteMapper quoteMapper = new QuoteMapperImpl();

    @InjectMocks
    QuoteStateController quoteStateController;

    @BeforeEach
    public void setUp() {
        super.setUp(quoteStateController);
        quoteService.quoteRepository = quoteRepository;
        quoteService.quoteMapper = quoteMapper;
        quoteStateService.quoteStateRepository = quoteStateRepository;
    }

    @Test
    void VoteUp_CreateNewQuoteState_Success() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteStateService).addUpVoteQuoteState(any(Quote.class), any(User.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.of(quote));
        when(quoteStateRepository.findUpVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.empty());
        when(quoteStateRepository.findDownVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.empty());

        var result = this.mvc.perform(post("/api/vote/up/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Up Vote for Quote [" + quote.getText() + "] by user [" + user.getUsername() + "]"));
    }

    @Test
    void VoteUp_ChangeQuoteStateVoteValue_Success() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteStateService).addUpVoteQuoteState(any(Quote.class), any(User.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.of(quote));
        when(quoteStateRepository.findUpVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.empty());
        when(quoteStateRepository.findDownVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.of(voteDownQuoteState));

        var result = this.mvc.perform(post("/api/vote/up/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Up Vote for Quote [" + quote.getText() + "] by user [" + user.getUsername() + "]"));
    }

    @Test
    void VoteDown_CreateNewQuoteState_Success() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteStateService).addDownVoteQuoteState(any(Quote.class), any(User.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.of(quote));
        when(quoteStateRepository.findDownVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.empty());
        when(quoteStateRepository.findUpVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.empty());

        var result = this.mvc.perform(post("/api/vote/down/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Down Vote for Quote [" + quote.getText() + "] by user [" + user.getUsername() + "]"));
    }

    @Test
    void VoteDown_ChangeQuoteStateVoteValue_Success() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteStateService).addDownVoteQuoteState(any(Quote.class), any(User.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.of(quote));
        when(quoteStateRepository.findDownVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.empty());
        when(quoteStateRepository.findUpVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.of(voteUpQuoteState));

        var result = this.mvc.perform(post("/api/vote/down/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Down Vote for Quote [" + quote.getText() + "] by user [" + user.getUsername() + "]"));
    }
}
