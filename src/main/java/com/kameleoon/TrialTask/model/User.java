package com.kameleoon.TrialTask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USERS")
@Setter
@Getter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false, length = 25)
    @NotNull
    private String username;

    @Column(nullable = false)
    @NotNull
    @ToString.Exclude
    private String password;

    @Column(unique = true, nullable = false)
    @NotNull
    private String email;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdOn;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<QuoteState> stats = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Quote> quotes = new HashSet<>();
}
