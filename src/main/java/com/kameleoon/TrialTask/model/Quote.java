package com.kameleoon.TrialTask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "QUOTES")
@Setter
@Getter
@ToString
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @NotNull
    private Long id;

    @Column(nullable = false, length = 128)
    @NotNull
    private String text;
}
