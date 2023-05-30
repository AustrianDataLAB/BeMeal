package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

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
