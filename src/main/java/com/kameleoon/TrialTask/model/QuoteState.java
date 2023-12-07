package com.kameleoon.TrialTask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "QUOTE_STATES", uniqueConstraints = { @UniqueConstraint(columnNames = { "quote_id", "user_id" }) })
@Setter
@Getter
@ToString
@NoArgsConstructor
public class QuoteState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quote_id")
    private Quote quote;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @NotNull
    private int voteValue;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant votedOn;

    public QuoteState(Quote quote, User user) {
        this.setUser(user);
        this.setQuote(quote);
    }
}
