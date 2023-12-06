package com.kameleoon.TrialTask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kameleoon.TrialTask.config.SecurityConfig;
import com.kameleoon.TrialTask.config.TestConfig;
import com.kameleoon.TrialTask.dto.QuoteDto;
import com.kameleoon.TrialTask.exception.EmailAlreadyExistException;
import com.kameleoon.TrialTask.exception.QuoteNotFoundException;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.repository.QuoteRepository;
import com.kameleoon.TrialTask.service.QuoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ QuoteController.class })
@Import({ SecurityConfig.class, TestConfig.class })
public class QuoteControllerTest extends AbstractTest{
    @MockBean
    QuoteService quoteService;

    @MockBean
    QuoteRepository quoteRepository;

    @InjectMocks
    QuoteController quoteController;

    @BeforeEach
    public void setUp() {
        super.setUp(quoteController);
        quoteService.quoteRepository = quoteRepository;
    }

    @Test
    void GetQuote_Success() throws Exception {
        doReturn(quote).when(quoteService).findQuoteById(any(Long.class));

        QuoteDto quoteDto = new QuoteDto(quote);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(quoteDto).replaceAll("[\\t\\s]", "");

        var result = this.mvc.perform(get("/api/quote/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(requestJson, result.getResponse().getContentAsString());
    }

    @Test
    void GetQuote_QuoteWithIdFound_ExceptionThrow() throws Exception {
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        doReturn(Optional.empty()).when(quoteRepository).findById(any(Long.class));

        this.mvc.perform(get("/api/quote/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote with id [" + quote.getId() + "] not found!", result.getResolvedException().getMessage()));
    }

    @Test
    void GetQuote_PathVariableTypeMismatch_ExceptionThrow() throws Exception {
        this.mvc.perform(get("/api/quote/{id}", "dfgt")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"dfgt\"", result.getResolvedException().getMessage()));
    }

    @Test
    void GetQuote_PathVariableMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(get("/api/quote/{id}", " ")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingPathVariableException))
                .andExpect(result -> assertEquals("Required URI template variable 'id' for method parameter type Long is present but converted to null", result.getResolvedException().getMessage()));
    }
}
