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
    void GetQuote_QuoteWithIdNotFound_ExceptionThrow() throws Exception {
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        doReturn(Optional.empty()).when(quoteRepository).findById(any(Long.class));

        long quoteId = 100;

        this.mvc.perform(get("/api/quote/{id}", quoteId)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote with id [" + quoteId + "] not found!", result.getResolvedException().getMessage()));
    }

    @Test
    void GetQuote_PathVariableTypeMismatch_ExceptionThrow() throws Exception {
        this.mvc.perform(get("/api/quote/{id}", "badId")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"badId\"", result.getResolvedException().getMessage()));
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
    void GetRandomQuote_QuoteNotFound_ExceptionThrow() throws Exception {
        doCallRealMethod().when(quoteService).getRandomQuote();
        doReturn(List.of()).when(quoteRepository).findAll();

        this.mvc.perform(get("/api/quote/rnd")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote not found!", result.getResolvedException().getMessage()));
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
    void AddNewQuote_QuoteContentDtoParameterMissing_ExceptionThrow() throws Exception {
            this.mvc.perform(post("/api/quote/add")
                    .headers(headers)
                    .principal(principal))
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof RequiredRequestParamIsMissingException))
            .andExpect(result -> assertEquals("Required request param QuoteContentDto is missing", result.getResolvedException().getMessage()));

    }

    @Test
    void AddNewQuote_QuoteContentDtoParameterNotValid_ExceptionThrow() throws Exception {
        var result = this.mvc.perform(post("/api/quote/add")
                        .contentType(APPLICATION_JSON_UTF8).content("{\"par\": \"badreq\"}")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Quote text must not be empty!"));
    }

    @Test
    void UpdateQuote_Success() throws Exception {
        QuoteContentDto quoteContentDto = new QuoteContentDto();
        quoteContentDto.setText("Test Quote!");
        Quote newQuote = new Quote(quoteContentDto);
        newQuote.setAuthor(user);

        doCallRealMethod().when(quoteService).updateQuote(any(Long.class), any(QuoteContentDto.class));
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
        assertEquals(quoteContentDto.getText(), quote.getText());
    }

    @Test
    void UpdateQuote_QuoteContentDtoParameterMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(put("/api/quote/update/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RequiredRequestParamIsMissingException))
                .andExpect(result -> assertEquals("Required request param QuoteContentDto is missing", result.getResolvedException().getMessage()));

    }

    @Test
    void UpdateQuote_QuoteDtoContentParameterNotValid_ExceptionThrow() throws Exception {
        var result = this.mvc.perform(put("/api/quote/update/{id}", 1)
                        .contentType(APPLICATION_JSON_UTF8).content("{\"par\": \"badreq\"}")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Quote text must not be empty!"));
    }

    @Test
    void UpdateQuote_QuoteWithIdNotFound_ExceptionThrow() throws Exception {
        doCallRealMethod().when(quoteService).updateQuote(any(Long.class), any(QuoteContentDto.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        doReturn(Optional.empty()).when(quoteRepository).findById(any(Long.class));

        QuoteContentDto quoteContentDto = new QuoteContentDto();
        quoteContentDto.setText("Test Quote!");
        long quoteId = 100;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(quoteContentDto);

        this.mvc.perform(put("/api/quote/update/{id}", quoteId)
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote with id [" + quoteId + "] not found!", result.getResolvedException().getMessage()));
    }

    @Test
    void UpdateQuote_PathVariableTypeMismatch_ExceptionThrow() throws Exception {
        QuoteContentDto quoteContentDto = new QuoteContentDto();
        quoteContentDto.setText("Test Quote!");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(quoteContentDto);

        this.mvc.perform(put("/api/quote/update/{id}", "badId")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"badId\"", result.getResolvedException().getMessage()));
    }

    @Test
    void UpdateQuote_PathVariableMissing_ExceptionThrow() throws Exception {
        QuoteContentDto quoteContentDto = new QuoteContentDto();
        quoteContentDto.setText("Test Quote!");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(quoteContentDto);

        this.mvc.perform(put("/api/quote/update/{id}", " ")
                        .contentType(APPLICATION_JSON_UTF8).content(requestJson)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingPathVariableException))
                .andExpect(result -> assertEquals("Required URI template variable 'id' for method parameter type Long is present but converted to null", result.getResolvedException().getMessage()));
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
    void DeleteQuote_AccessToDeleteQuoteDenied_ExceptionThrow() throws Exception {
        long quoteId = 200L;
        User anotherUser = new User();
        anotherUser.setId(2L);
        quote.setAuthor(anotherUser);

        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteService).deleteQuote(any(User.class), any(Long.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.of(quote));

        this.mvc.perform(delete("/api/quote/delete/{id}", quoteId)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessToResourceDeniedException))
                .andExpect(result -> assertEquals("You don't have rights to delete quote with id [" + quoteId + "]", result.getResolvedException().getMessage()));
    }

    @Test
    void DeleteQuote_QuoteWithIdNotFound_ExceptionThrow() throws Exception {
        long quoteId = 200L;

        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteService).deleteQuote(any(User.class), any(Long.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        this.mvc.perform(delete("/api/quote/delete/{id}", quoteId)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote with id [" + quoteId + "] not found!", result.getResolvedException().getMessage()));
    }

    @Test
    void DeleteQuote_PathVariableTypeMismatch_ExceptionThrow() throws Exception {
        this.mvc.perform(delete("/api/quote/delete/{id}", "badId")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"badId\"", result.getResolvedException().getMessage()));
    }

    @Test
    void DeleteQuote_PathVariableMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(delete("/api/quote/delete/{id}", " ")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingPathVariableException))
                .andExpect(result -> assertEquals("Required URI template variable 'id' for method parameter type Long is present but converted to null", result.getResolvedException().getMessage()));
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

        var result = this.mvc.perform(post("/api/quote/vote/up/{id}", 1)
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

        var result = this.mvc.perform(post("/api/quote/vote/up/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Up Vote for Quote [" + quote.getText() + "] by user [" + user.getUsername() + "]"));
    }

    @Test
    void VoteUp_QuoteWithIdNotFound_ExceptionThrow() throws Exception {
        long quoteId = 200L;

        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteStateService).addUpVoteQuoteState(any(Quote.class), any(User.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        this.mvc.perform(post("/api/quote/vote/up/{id}", quoteId)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote with id [" + quoteId + "] not found!", result.getResolvedException().getMessage()));
    }

    @Test
    void VoteUp_UpVoteQuoteStateAlreadyExist_ExceptionThrow() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteStateService).addUpVoteQuoteState(any(Quote.class), any(User.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.of(quote));
        when(quoteStateRepository.findUpVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.of(voteUpQuoteState));

        this.mvc.perform(post("/api/quote/vote/up/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UpVoteQuoteStateAlreadyExistException))
                .andExpect(result -> assertEquals("User [" + user.getUsername() + "] already voted for Quote [" + quote.getText() + "]", result.getResolvedException().getMessage()));
    }

    @Test
    void VoteUp_PathVariableTypeMismatch_ExceptionThrow() throws Exception {
        this.mvc.perform(post("/api/quote/vote/up/{id}", "badId")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"badId\"", result.getResolvedException().getMessage()));
    }

    @Test
    void VoteUp_PathVariableMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(post("/api/quote/vote/up/{id}", " ")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingPathVariableException))
                .andExpect(result -> assertEquals("Required URI template variable 'id' for method parameter type Long is present but converted to null", result.getResolvedException().getMessage()));
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

        var result = this.mvc.perform(post("/api/quote/vote/down/{id}", 1)
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

        var result = this.mvc.perform(post("/api/quote/vote/down/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), containsString("Down Vote for Quote [" + quote.getText() + "] by user [" + user.getUsername() + "]"));
    }

    @Test
    void VoteDown_QuoteWithIdNotFound_ExceptionThrow() throws Exception {
        long quoteId = 200L;

        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteStateService).addDownVoteQuoteState(any(Quote.class), any(User.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        this.mvc.perform(post("/api/quote/vote/down/{id}", quoteId)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof QuoteNotFoundException))
                .andExpect(result -> assertEquals("Quote with id [" + quoteId + "] not found!", result.getResolvedException().getMessage()));
    }

    @Test
    void VoteDown_UpVoteQuoteStateAlreadyExist_ExceptionThrow() throws Exception {
        when(principal.getName()).thenReturn(user.getUsername());
        when(userService.findUserByUserName(user.getUsername())).thenReturn(user);
        doCallRealMethod().when(quoteStateService).addDownVoteQuoteState(any(Quote.class), any(User.class));
        doCallRealMethod().when(quoteService).findQuoteById(any(Long.class));
        when(quoteRepository.findById(any(Long.class))).thenReturn(Optional.of(quote));
        when(quoteStateRepository.findDownVoteQuoteStateByUserAndQuote(quote, user)).thenReturn(Optional.of(voteDownQuoteState));

        this.mvc.perform(post("/api/quote/vote/down/{id}", 1)
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DownVoteQuoteStateAlreadyExistException))
                .andExpect(result -> assertEquals("User [" + user.getUsername() + "] already voted for Quote [" + quote.getText() + "]", result.getResolvedException().getMessage()));
    }

    @Test
    void VoteDown_PathVariableTypeMismatch_ExceptionThrow() throws Exception {
        this.mvc.perform(post("/api/quote/vote/down/{id}", "badId")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"badId\"", result.getResolvedException().getMessage()));
    }

    @Test
    void VoteDown_PathVariableMissing_ExceptionThrow() throws Exception {
        this.mvc.perform(post("/api/quote/vote/down/{id}", " ")
                        .headers(headers)
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingPathVariableException))
                .andExpect(result -> assertEquals("Required URI template variable 'id' for method parameter type Long is present but converted to null", result.getResolvedException().getMessage()));
    }
}
