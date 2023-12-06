package com.kameleoon.TrialTask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "QUOTE_STATS")
@Setter
@Getter
@ToString
public class QuoteStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @NotNull
    private int voteValue;

    @Column(nullable = false, updatable = false)
    @NotNull
    @CreationTimestamp
    private Instant votedOn;
}
