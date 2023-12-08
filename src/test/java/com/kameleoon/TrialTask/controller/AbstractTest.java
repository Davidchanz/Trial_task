package com.kameleoon.TrialTask.controller;

import com.kameleoon.TrialTask.advice.ExceptionHandlerAdvice;
import com.kameleoon.TrialTask.config.SecurityConfig;
import com.kameleoon.TrialTask.config.TestConfig;
import com.kameleoon.TrialTask.dto.GraphDto;
import com.kameleoon.TrialTask.dto.VoteDto;
import com.kameleoon.TrialTask.model.CustomUserDetails;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.model.QuoteState;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.security.JwtAuthenticationEntryPoint;
import com.kameleoon.TrialTask.security.JwtTokenProvider;
import com.kameleoon.TrialTask.service.CustomUserDetailsService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.Charset;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Import({ SecurityConfig.class, TestConfig.class })
public abstract class AbstractTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    public MockMvc mvc;

    @MockBean
    public CustomUserDetailsService customUserDetailsService;

    @MockBean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public JwtTokenProvider jwtTokenProvider;

    public String token;

    public HttpHeaders headers;

    @Mock
    public Principal principal;

    public User user;

    public Quote quote;

    public QuoteState voteUpQuoteState;

    public QuoteState voteDownQuoteState;

    public GraphDto graphDto;

    public List<VoteDto> votes;

    public CustomUserDetails customUserDetails = getCustomUserDetails();

    {
        user = getUser();
        quote = getQuote();
        voteUpQuoteState = getVoteUpState();
        voteDownQuoteState = getVoteDownState();
        votes = getVotes();
        graphDto = getGraphDto();
    }

    public void setUp(Object controller){
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ExceptionHandlerAdvice()).build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        token = jwtTokenProvider.generateToken(customUserDetails);
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
    }

    public CustomUserDetails getCustomUserDetails(){
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setEmail("admin@email.com");
        customUserDetails.setPassword("password");
        customUserDetails.setUsername("admin");
        customUserDetails.setId(1L);
        return customUserDetails;
    }

    public User getUser(){
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setEmail("email");
        user.setUsername("testuser");
        return user;
    }

    public Quote getQuote(){
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setText("Quote!");
        quote.setAuthor(user);
        return quote;
    }

    public QuoteState getVoteUpState(){
        QuoteState voteUpQuoteState = new QuoteState();
        voteUpQuoteState.setId(1L);
        voteUpQuoteState.setVoteValue(1);
        voteUpQuoteState.setQuote(getQuote());
        voteUpQuoteState.setUser(getUser());
        return voteUpQuoteState;
    }

    public QuoteState getVoteDownState(){
        QuoteState voteUpQuoteState = new QuoteState();
        voteUpQuoteState.setId(1L);
        voteUpQuoteState.setVoteValue(-1);
        voteUpQuoteState.setQuote(getQuote());
        voteUpQuoteState.setUser(getUser());
        return voteUpQuoteState;
    }

    private List<VoteDto> getVotes() {
        List<VoteDto> data = new ArrayList<>();
        data.add(new VoteDto(Instant.now(), 1));
        data.add(new VoteDto(Instant.now(), 1));
        data.add(new VoteDto(Instant.now(), -1));
        return data;
    }

    public GraphDto getGraphDto(){
        GraphDto graphDto = new GraphDto();
        List<VoteDto> data = new ArrayList<>();
        data.add(new VoteDto(votes.get(0).getVotedOn(), 1));
        data.add(new VoteDto(votes.get(1).getVotedOn(), 2));
        data.add(new VoteDto(votes.get(2).getVotedOn(), 1));
        graphDto.setData(data);
        graphDto.setMaxRating(data.get(data.size()-1).getVoteValue());
        graphDto.setMinRating(data.get(0).getVoteValue());
        graphDto.setMaxTime(data.get(data.size()-1).getVotedOn());
        graphDto.setMinTime(data.get(0).getVotedOn());
        return graphDto;
    }
}
