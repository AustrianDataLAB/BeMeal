package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "participant_id")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Participant extends PlatformUser {

    public Participant(Long id, String email, byte[] password, String username, Boolean isAdmin, List<League> ownerOf, String postalCode, Integer wins, Region region, LocalDateTime registered, List<Submission> submissions, List<Submission> votes, List<League> leagues) {
        super(id, email, password, username, isAdmin, ownerOf);
        this.postalCode = postalCode;
        this.wins = wins;
        this.region = region;
        this.registered = registered;
        this.submissions = submissions;
        this.votes = votes;
        this.leagues = leagues;
    }

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
    @ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
    private List<League> leagues;
}
