package com.kameleoon.TrialTask.service;

import com.kameleoon.TrialTask.dto.QuoteContentDto;
import com.kameleoon.TrialTask.exception.AccessToResourceDeniedException;
import com.kameleoon.TrialTask.exception.QuoteNotFoundException;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.repository.QuoteRepository;
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
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@ActiveProfiles("TEST")
public class QuoteServiceTest {

    @MockBean
    QuoteService quoteService;

    @MockBean
    QuoteRepository quoteRepository;

    @BeforeEach
    void setUp(){
        quoteService.quoteRepository = quoteRepository;
    }

    @Test
    void QuoteWithIdNotFound_ExceptionThrow(){
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        doReturn(Optional.empty()).when(quoteRepository).findById(any(Long.class));

        Exception exception = assertThrows(QuoteNotFoundException.class, () -> quoteService.findQuoteById(1L));
        String expectedMessage = "Quote with id [" + 1 + "] not found!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void QuotesNotFound_ExceptionThrow(){
        doCallRealMethod().when(quoteService).getRandomQuote();
        doReturn(List.of()).when(quoteRepository).findAll();

        Exception exception = assertThrows(QuoteNotFoundException.class, () -> quoteService.getRandomQuote());
        String expectedMessage = "Quotes not found!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void AccessToDeleteQuoteDenied_ExceptionThrow() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setAuthor(user2);

        doCallRealMethod().when(quoteService).deleteQuote(any(User.class), any(Long.class));
        doReturn(quote).when(quoteService).findQuoteById(any(Long.class));

        Exception exception = assertThrows(AccessToResourceDeniedException.class, () -> quoteService.deleteQuote(user1, 1L));
        String expectedMessage = "You don't have rights to delete Quote with id [" + quote.getId() + "]";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void AccessToUpdateQuoteDenied_ExceptionThrow() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setAuthor(user2);

        doCallRealMethod().when(quoteService).updateQuote(any(User.class), any(Long.class), any(QuoteContentDto.class));
        doReturn(quote).when(quoteService).findQuoteById(any(Long.class));

        Exception exception = assertThrows(AccessToResourceDeniedException.class, () -> quoteService.updateQuote(user1, quote.getId(), new QuoteContentDto()));
        String expectedMessage = "You don't have rights to update Quote with id [" + 1 + "]";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
