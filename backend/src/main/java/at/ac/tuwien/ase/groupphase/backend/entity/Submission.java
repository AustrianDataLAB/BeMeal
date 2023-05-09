package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToMany(mappedBy = "submission", cascade = CascadeType.REMOVE)
    private List<ParticipantSubmissionVote> upVotes;
}
