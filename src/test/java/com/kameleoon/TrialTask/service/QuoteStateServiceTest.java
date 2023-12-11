package com.kameleoon.TrialTask.service;

import com.kameleoon.TrialTask.dto.QuoteContentDto;
import com.kameleoon.TrialTask.exception.AccessToResourceDeniedException;
import com.kameleoon.TrialTask.exception.DownVoteQuoteStateAlreadyExistException;
import com.kameleoon.TrialTask.exception.QuoteNotFoundException;
import com.kameleoon.TrialTask.exception.UpVoteQuoteStateAlreadyExistException;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.model.QuoteState;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.repository.QuoteRepository;
import com.kameleoon.TrialTask.repository.QuoteStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("TEST")
public class QuoteStateServiceTest {

    @MockBean
    QuoteStateService quoteStateService;

    @MockBean
    QuoteStateRepository quoteStateRepository;

    @BeforeEach
    void setUp(){
        quoteStateService.quoteStateRepository = quoteStateRepository;
    }

    @Test
    void UpVoteQuoteStateAlreadyExist_ExceptionThrow(){
        Quote quote = new Quote();
        quote.setText("TestQuoteText");
        User user = new User();
        user.setUsername("TestUserName");

        QuoteState voteUpQuoteState = new QuoteState();

        doCallRealMethod().when(quoteStateService).addUpVoteQuoteState(any(Quote.class), any(User.class));
        when(quoteStateRepository.findUpVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.of(voteUpQuoteState));

        Exception exception = assertThrows(UpVoteQuoteStateAlreadyExistException.class, () -> quoteStateService.addUpVoteQuoteState(quote, user));
        String expectedMessage = "User [" + user.getUsername() + "] already voted up for Quote [" + quote.getText() + "]";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void DownVoteQuoteStateAlreadyExist_ExceptionThrow(){
        Quote quote = new Quote();
        quote.setText("TestQuoteText");
        User user = new User();
        user.setUsername("TestUserName");

        QuoteState voteDownQuoteState = new QuoteState();

        doCallRealMethod().when(quoteStateService).addDownVoteQuoteState(any(Quote.class), any(User.class));
        when(quoteStateRepository.findDownVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.of(voteDownQuoteState));

        Exception exception = assertThrows(DownVoteQuoteStateAlreadyExistException.class, () -> quoteStateService.addDownVoteQuoteState(quote, user));
        String expectedMessage = "User [" + user.getUsername() + "] already voted down for Quote [" + quote.getText() + "]";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
