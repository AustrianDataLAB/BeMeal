package at.ac.tuwien.ase.groupphase.backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionWithUpvotes {
    private Submission submission;
    private Long upvotes = 0L;

    public SubmissionWithUpvotes(Submission submission, Long upvotes) {
        this.submission = submission;
        this.upvotes = upvotes;
    }
}
