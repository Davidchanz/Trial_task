package com.kameleoon.TrialTask.repository;

import com.kameleoon.TrialTask.dto.QuoteStateDto;
import com.kameleoon.TrialTask.dto.VoteDto;
import com.kameleoon.TrialTask.model.Quote;
import com.kameleoon.TrialTask.model.QuoteState;
import com.kameleoon.TrialTask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuoteStateRepository extends JpaRepository<QuoteState, Long> {
    @Query("select q from QuoteState q where q.quote = ?1 and q.user = ?2 and q.voteValue = 1")
    Optional<QuoteState> findUpVoteQuoteStateByUserAndQuote(Quote quote, User user);

    @Query("select q from QuoteState q where q.quote = ?1 and q.user = ?2 and q.voteValue = -1")
    Optional<QuoteState> findDownVoteQuoteStateByUserAndQuote(Quote quote, User user);

    @Query("SELECT new com.kameleoon.TrialTask.dto.QuoteStateDto(q.quote, SUM(q.voteValue)) "
            + "FROM QuoteState AS q GROUP BY q.quote ORDER BY SUM(q.voteValue) DESC LIMIT 10")
    List<QuoteStateDto> findTop10();

    @Query("SELECT new com.kameleoon.TrialTask.dto.QuoteStateDto(q.quote, SUM(q.voteValue)) "
            + "FROM QuoteState AS q GROUP BY q.quote ORDER BY SUM(q.voteValue) ASC LIMIT 10")
    List<QuoteStateDto> findWorse10();

    @Query("SELECT new com.kameleoon.TrialTask.dto.VoteDto(q.votedOn, q.voteValue) "
            + "FROM QuoteState AS q WHERE q.quote = ?1 ORDER BY q.votedOn ASC")
    List<VoteDto> findQuoteVoteGraphData(Quote quote);
}
