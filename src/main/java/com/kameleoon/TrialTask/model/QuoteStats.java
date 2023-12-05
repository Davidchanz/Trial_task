package com.kameleoon.TrialTask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "QUOTE_STATS")
@Setter
@Getter
@ToString
public class QuoteStats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @NotNull
    private Long id;

    @OneToOne
    private Quote quote;

    @Column(nullable = false)
    @NotNull
    private int score;

    @OneToOne
    private User author;

    //private chart_over_time ???
}
