package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode
public class League {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Used for identifying the league with an id which cannot be guessed by iterating through numbers. Useful for
     * things such as invitation links.
     */
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID hiddenIdentifier;

    @Column(nullable = false)
    private GameMode gameMode;
    @Column
    private Region region;
    @Column(nullable = false)
    private Integer challengeDuration;

    @Column(nullable = false)
    private String name;

    @OneToMany
    private List<Challenge> challenges;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Participant> participants;
}
