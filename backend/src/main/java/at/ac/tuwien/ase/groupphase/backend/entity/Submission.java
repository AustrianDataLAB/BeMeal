package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID picture;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(optional = false)
    private Participant participant;

    @ManyToOne(optional = false)
    private Challenge challenge;

    @ManyToMany
    private List<Participant> upVotes;
}
