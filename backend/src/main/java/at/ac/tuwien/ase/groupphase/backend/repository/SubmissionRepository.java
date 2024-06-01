package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.Submission;
import at.ac.tuwien.ase.groupphase.backend.entity.SubmissionWithUpvotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s" + " WHERE s.challenge.id = :challengeId"
            + " AND s.participant.id != :participantId"
            + " AND s.id NOT IN (SELECT v.submission.id FROM ParticipantSubmissionVote v" + " INNER JOIN Submission s"
            + " ON v.submission.id = s.id"
            + " WHERE s.participant.id != :participantId AND v.participant.id = :participantId)")
    List<Submission> getSubmissionNotUpvotedYetByUser(Long challengeId, Long participantId);

    @Query("SELECT NEW at.ac.tuwien.ase.groupphase.backend.entity.SubmissionWithUpvotes(s, COUNT(*)) FROM Submission s"
            + " INNER JOIN ParticipantSubmissionVote sv"
            + " ON s.id = sv.submission.id WHERE sv.isUpvote = TRUE AND s.challenge.id = :challengeId"
            + " GROUP BY s HAVING COUNT(*) = (SELECT MAX(upvotes_c.c) FROM ("
            + " SELECT COUNT(*) AS c FROM ParticipantSubmissionVote AS sv2 INNER JOIN Submission s2"
            + " ON s2.id = sv2.submission.id WHERE sv2.isUpvote = TRUE"
            + " AND s2.challenge.id = :challengeId GROUP BY s2) AS upvotes_c)")
    List<SubmissionWithUpvotes> getWinnerSubmissionOfChallenge(Long challengeId);

    @Query("SELECT s FROM Submission s WHERE s.challenge.id = :challengeId AND s.participant.id = :participantId ORDER BY s.id DESC LIMIT 1")
    Submission getcurrentSubmission(Long challengeId, Long participantId);
}
