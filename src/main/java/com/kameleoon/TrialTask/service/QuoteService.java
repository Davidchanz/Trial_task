package com.kameleoon.TrialTask.service;

import com.kameleoon.TrialTask.dto.QuoteContentDto;
import com.kameleoon.TrialTask.exception.AccessToResourceDeniedException;
import com.kameleoon.TrialTask.exception.QuoteNotFoundException;
import com.kameleoon.TrialTask.mapper.QuoteMapper;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.model.QuoteState;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.repository.QuoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Transactional
@Service
public class QuoteService {
    @Autowired
    public QuoteRepository quoteRepository;

    @Autowired
    public QuoteMapper quoteMapper;

    public Quote findQuoteById(long id) {
        return quoteRepository.findById(id).orElseThrow(() -> new QuoteNotFoundException("Quote with id [" + id + "] not found!"));
    }

    public Quote getRandomQuote() {
        var quotes = quoteRepository.findAll();
        if(quotes.isEmpty())
            throw new QuoteNotFoundException("Quotes not found!");
        return quotes.get(new Random().nextInt(quotes.size()));
    }

    public void addNewQuote(Quote quote) {
        quoteRepository.save(quote);
    }

    public void updateQuote(User user, Long id, QuoteContentDto quoteContentDto) {
        Quote quote = findQuoteById(id);
        if(!Objects.equals(quote.getAuthor().getId(), user.getId()))
            throw new AccessToResourceDeniedException("You don't have rights to update Quote with id [" + id + "]");

        quoteMapper.updateQuoteFromDto(quoteContentDto, quote);
        quoteRepository.save(quote);
    }

    public Quote deleteQuote(User user, Long id) {
        Quote quote = findQuoteById(id);
        if(!Objects.equals(quote.getAuthor().getId(), user.getId()))
            throw new AccessToResourceDeniedException("You don't have rights to delete Quote with id [" + id + "]");
        quoteRepository.delete(quote);
        return quote;
    }
}
