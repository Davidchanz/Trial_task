package com.kameleoon.TrialTask.service;

import com.kameleoon.TrialTask.dto.QuoteStateDto;
import com.kameleoon.TrialTask.dto.VoteDto;
import com.kameleoon.TrialTask.exception.DownVoteQuoteStateAlreadyExistException;
import com.kameleoon.TrialTask.exception.UpVoteQuoteStateAlreadyExistException;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.model.QuoteState;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.repository.QuoteStateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class QuoteStateService {
    @Autowired
    public QuoteStateRepository quoteStateRepository;

    public void addUpVoteQuoteState(Quote quote, User user) {
        if(quoteStateRepository.findUpVoteQuoteStateByUserAndQuote(quote, user).isPresent())
            throw new UpVoteQuoteStateAlreadyExistException(
                    "User [" + user.getUsername() + "] already voted up for Quote [" + quote.getText() + "]");

        var downVoteQuoteState = quoteStateRepository.findDownVoteQuoteStateByUserAndQuote(quote, user);
        if(downVoteQuoteState.isPresent()){
            QuoteState quoteState = downVoteQuoteState.get();
            quoteState.setVoteValue(1);
            quoteStateRepository.save(quoteState);
        }else {
            QuoteState quoteState = new QuoteState(quote, user);
            quoteState.setVoteValue(1);
            quoteStateRepository.save(quoteState);
        }
    }

    public void addDownVoteQuoteState(Quote quote, User user) {
        if(quoteStateRepository.findDownVoteQuoteStateByUserAndQuote(quote, user).isPresent())
            throw new DownVoteQuoteStateAlreadyExistException(
                    "User [" + user.getUsername() + "] already voted down for Quote [" + quote.getText() + "]");

        var upVoteQuoteState= quoteStateRepository.findUpVoteQuoteStateByUserAndQuote(quote, user);
        if(upVoteQuoteState.isPresent()){
            QuoteState quoteState = upVoteQuoteState.get();
            quoteState.setVoteValue(-1);
            quoteStateRepository.save(quoteState);
        }else {
            QuoteState quoteState = new QuoteState(quote, user);
            quoteState.setVoteValue(-1);
            quoteStateRepository.save(quoteState);
        }
    }

    public List<QuoteStateDto> getTop10QuoteStates() {
        return quoteStateRepository.findTop10();
    }

    public List<QuoteStateDto> getWorse10QuoteStates() {
        return quoteStateRepository.findWorse10();
    }

    public List<VoteDto> getQuoteVoteGraphData(Quote quote) {
        return quoteStateRepository.findQuoteVoteGraphData(quote);
    }
}
