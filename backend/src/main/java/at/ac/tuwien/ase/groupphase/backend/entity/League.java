package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode
public class League {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)        // ToDo: @Enumerated(EnumType.STRING) is ignored?? See: https://www.baeldung.com/jpa-persisting-enums-in-jpa
    private GameMode gameMode;
    @Column
    @Enumerated(EnumType.ORDINAL)
    private Region region;
    @Column(nullable = false)
    private Integer challengeDuration;

    @OneToMany
    private List<Challenge> challenges;
    @ManyToMany
    private List<Participant> participants;
}
