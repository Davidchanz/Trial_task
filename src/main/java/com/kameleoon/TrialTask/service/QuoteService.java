package com.kameleoon.TrialTask.service;

import com.kameleoon.TrialTask.exception.QuoteNotFoundException;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.repository.QuoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class QuoteService {
    @Autowired
    public QuoteRepository quoteRepository;

    public Quote findQuoteById(long id) {
        return quoteRepository.findById(id).orElseThrow(() -> new QuoteNotFoundException("Quote with id [" + id + "] not found!"));
    }
}
