package com.kameleoon.TrialTask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kameleoon.TrialTask.config.SecurityConfig;
import com.kameleoon.TrialTask.config.TestConfig;
import com.kameleoon.TrialTask.dto.QuoteContentDto;
import com.kameleoon.TrialTask.dto.QuoteDto;
import com.kameleoon.TrialTask.exception.*;
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
import org.mapstruct.control.MappingControl;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ QuoteController.class })
@Import({ SecurityConfig.class, TestConfig.class })
public class QuoteControllerTest extends AbstractTest{
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
    void GetRandomQuote_Success() throws Exception {
        doReturn(quote).when(quoteService).getRandomQuote();

        QuoteDto quoteDto = new QuoteDto(quote);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(quoteDto).replaceAll("[\\t\\s]", "");


        var result = this.mvc.perform(get("/api/quote/rnd")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(requestJson, result.getResponse().getContentAsString());
    }

    @Test
    void AddNewQuote_Success() throws Exception {
        QuoteContentDto quoteContentDto = new QuoteContentDto();
        quoteContentDto.setText("Test Quote!");
        Quote newQuote = new Quote(quoteContentDto);
        newQuote.setAuthor(user);

        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteService).addNewQuote(any(Quote.class));
        when(quoteRepository.save(any(Quote.class))).thenReturn(newQuote);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(quoteContentDto).replaceAll("[\\t\\s]", "");
        String responseJson = ow.writeValueAsString(new QuoteDto(newQuote)).replaceAll("[\\t\\s]", "");


        var result = this.mvc.perform(post("/api/quote/add")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(responseJson, result.getResponse().getContentAsString());
    }

    @Test
    void UpdateQuote_Success() throws Exception {
        QuoteContentDto quoteContentDto = new QuoteContentDto();
        quoteContentDto.setText("Test Quote!");
        Quote newQuote = new Quote(quoteContentDto);
        newQuote.setAuthor(user);

        doCallRealMethod().when(quoteService).updateQuote(any(User.class), any(Long.class), any(QuoteContentDto.class));
        when(quoteService.findQuoteById(any(Long.class))).thenReturn(quote);
        when(quoteRepository.save(any(Quote.class))).thenReturn(quote);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(quoteContentDto);

        var result = this.mvc.perform(put("/api/quote/update/{id}", 1)
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("New Quote content is [Test Quote!]"));
        assertEquals(quoteContentDto.getText(), newQuote.getText());
    }

    @Test
    void DeleteQuote_Success() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteService).deleteQuote(any(User.class), any(Long.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.of(quote));

        var result = this.mvc.perform(delete("/api/quote/delete/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Quote [" + quote.getText() + "] was deleted!"));
    }

    @Test
    void GetQuoteGraph_Success() throws Exception {
        when(quoteStateService.getQuoteVoteGraphData(any(Quote.class))).thenReturn(votes);
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.of(quote));

        this.mvc.perform(get("/api/quote/graph/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk());
    }

}

