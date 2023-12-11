package com.kameleoon.TrialTask;

import com.kameleoon.TrialTask.config.SecurityConfig;
import com.kameleoon.TrialTask.config.TestConfig;
import com.kameleoon.TrialTask.controller.AbstractTest;
import com.kameleoon.TrialTask.controller.QuoteController;
import com.kameleoon.TrialTask.exception.RequiredRequestParamIsMissingException;
import com.kameleoon.TrialTask.mapper.QuoteMapper;
import com.kameleoon.TrialTask.mapper.QuoteMapperImpl;
import com.kameleoon.TrialTask.model.Quote;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ Quote.class })
@Import({ SecurityConfig.class, TestConfig.class })
public class RequestParamTest extends AbstractTest {

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
    QuoteController quoteController;

    @BeforeEach
    public void setUp() {
        super.setUp(quoteController);
        quoteService.quoteRepository = quoteRepository;
        quoteService.quoteMapper = quoteMapper;
        quoteStateService.quoteStateRepository = quoteStateRepository;
    }


    @Test
    void PathVariableTypeMismatch_ExceptionThrow() throws Exception {
        this.mvc.perform(get("/api/quote/{id}", "badId")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"badId\"", result.getResolvedException().getMessage()));
    }

    @Test
    void PathVariableMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(get("/api/quote/{id}", " ")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingPathVariableException))
                .andExpect(result -> assertEquals("Required URI template variable 'id' for method parameter type Long is present but converted to null", result.getResolvedException().getMessage()));
    }

    @Test
    void RequestBodyParameterMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(post("/api/quote/add")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RequiredRequestParamIsMissingException))
                .andExpect(result -> assertEquals("Required request param QuoteContentDto is missing", result.getResolvedException().getMessage()));

    }

    @Test
    void RequestBodyParameterNotValid_ExceptionThrow() throws Exception {
        var result = this.mvc.perform(post("/api/quote/add")
                        .contentType(APPLICATION_JSON_UTF8).content("{\"par\": \"badreq\"}")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Quote text must not be empty!"));
    }

}
