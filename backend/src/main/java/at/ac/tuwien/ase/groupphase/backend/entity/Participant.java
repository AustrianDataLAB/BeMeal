package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "participant_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Participant extends PlatformUser {
    @Column(nullable = false)
    private String postalCode;
    @Column(nullable = false)
    private Integer wins = 0;
    @Column(nullable = false)
    private Region region;
    @Column(nullable = false)
    private LocalDateTime registered;

    @OneToMany
    private List<Submission> submissions;
    @ManyToMany(mappedBy = "upVotes")
    private List<Submission> votes;
    @ManyToMany(mappedBy = "participants")
    private List<League> leagues;
}
