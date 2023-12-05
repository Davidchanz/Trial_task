package com.kameleoon.TrialTask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "USERS")
@Setter
@Getter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @NotNull
    private Long id;

    @Column(unique = true, nullable = false, length = 25)
    @NotNull
    private String username;

    @Column(nullable = false)
    @NotNull
    private String password;

    @Column(unique = true, nullable = false)
    @NotNull
    private String email;

    @Column(nullable = false)
    @NotNull
    private Instant registered = Instant.now();
}
