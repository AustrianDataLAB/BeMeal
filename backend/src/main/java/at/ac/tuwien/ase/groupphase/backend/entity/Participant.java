package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@PrimaryKeyJoinColumn(name = "participant_id")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Participant extends PlatformUser {

    public Participant(Long id, String email, byte[] password, String username, Boolean isAdmin, List<League> ownerOf,
            String postalCode, Map<Long, Integer> wins, Region region, LocalDateTime registered, List<Submission> submissions,
            List<ParticipantSubmissionVote> votes, List<League> leagues) {
        super(id, email, password, username, isAdmin, null, ownerOf);
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
    @ElementCollection
    private Map<Long,Integer> wins = new HashMap<>();
    @Column(nullable = false)
    private Region region;
    @Column(nullable = false)
    private LocalDateTime registered;

    @OneToMany
    private List<Submission> submissions;
    @OneToMany(mappedBy = "participant")
    private List<ParticipantSubmissionVote> votes;
    @ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
    private List<League> leagues;

    @ManyToOne
    @JoinColumn(name = "postalCode", referencedColumnName = "postalCode", insertable = false, updatable = false)
    private CommunityIdentification communityIdentification;
}
