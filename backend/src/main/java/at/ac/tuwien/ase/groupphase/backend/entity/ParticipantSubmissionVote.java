package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(VoteKey.class)
public class ParticipantSubmissionVote {

    @ManyToOne
    @Id
    private Participant participant;

    @ManyToOne
    @Id
    private Submission submission;

    @Column(name = "is_upvote")
    private boolean isUpvote;
}
