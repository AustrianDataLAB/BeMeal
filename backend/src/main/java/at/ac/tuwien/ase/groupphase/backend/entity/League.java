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
    private GameMode gameMode;
    @Column
    private Region region;
    @Column(nullable = false)
    private Integer challengeDuration;

    @Column(nullable = false)
    private String name;

    @OneToMany
    private List<Challenge> challenges;
    @ManyToMany
    private List<Participant> participants;
}
