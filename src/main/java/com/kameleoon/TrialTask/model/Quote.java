package com.kameleoon.TrialTask.model;

import com.kameleoon.TrialTask.dto.QuoteContentDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 128)
    @NotNull
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    private User author;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quote", fetch = FetchType.LAZY)
    private Set<QuoteState> stats = new HashSet<>();

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdOn;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant lastUpdatedOn;

    public Quote(QuoteContentDto quoteContentDto) {
        this.setText(quoteContentDto.getText());
    }
}
