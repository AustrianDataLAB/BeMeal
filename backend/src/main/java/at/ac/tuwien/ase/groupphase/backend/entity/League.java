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
    @Column(nullable = false)
    private UUID hiddenIdentifier = UUID.randomUUID();

    @Column(nullable = false)
    // ToDo: @Enumerated(EnumType.STRING) is ignored?? See: https://www.baeldung.com/jpa-persisting-enums-in-jpa
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
}
