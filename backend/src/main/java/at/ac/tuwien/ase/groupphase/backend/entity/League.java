package at.ac.tuwien.ase.groupphase.backend.entity;

import at.ac.tuwien.ase.groupphase.backend.entity.generator.ExplicitIdSequence;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class League {
    @Id
    @ExplicitIdSequence(name = "league_seq")
    private Long id;

    /**
     * Used for identifying the league with an id which cannot be guessed by iterating through numbers. Useful for
     * things such as invitation links.
     */
    @Column(nullable = false)
    private UUID hiddenIdentifier = UUID.randomUUID();

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private GameMode gameMode;
    @Column
    @Enumerated(EnumType.ORDINAL)
    private Region region;
    @Column(nullable = false)
    private Integer challengeDuration;

    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "league")
    private List<Challenge> challenges;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Participant> participants;

    public League(final Long id, final GameMode gameMode, final Region region, final Integer challengeDuration,
            final UUID hiddenIdentifier, final String name) {
        this(id, hiddenIdentifier, gameMode, region, challengeDuration, name, List.of(), List.of());
    }
}
