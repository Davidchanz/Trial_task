package com.kameleoon.TrialTask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "QUOTES")
@Setter
@Getter
@ToString
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 128)
    @NotNull
    private String text;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User author;

    @Column(name = "b_archive", nullable = false)
    @NotNull
    private boolean archiveFlag = false;

    @ToString.Exclude
    @OneToMany(mappedBy = "quote", fetch = FetchType.LAZY)
    private Set<QuoteStats> stats = new HashSet<>();

    @Column(nullable = false, updatable = false)
    @NotNull
    @CreationTimestamp
    private Instant createdOn;

    @Column(nullable = false)
    @NotNull
    @UpdateTimestamp
    private Instant lastUpdatedOn;
}
