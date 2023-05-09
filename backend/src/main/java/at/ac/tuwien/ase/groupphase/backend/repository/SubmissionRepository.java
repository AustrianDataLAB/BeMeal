package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.Submission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends CrudRepository<Submission, Long> {

    // ToDo: Handle challenge date
    // max(Enddate) < currentDate (use findLastEndedChallenge in LeagueRepository)
    /*
     * SELECT * FROM Submission s WHERE s.challenge_id = 2 AND s.participant_participant_id != 4 AND s.id NOT IN (SELECT
     * v.submission_id FROM Participant_Submission_Vote v INNER JOIN Submission s ON v.submission_id = s.id WHERE
     * s.participant_participant_id != 4);
     *
     * SELECT TOP(1) id FROM Challenge WHERE league_id = 2 ORDER BY END_DATE DESC;
     */
    @Query("SELECT s FROM Submission s" + " WHERE s.challenge.id = :challengeId"
            + " AND s.participant.id != :participantId"
            + " AND s.id NOT IN (SELECT v.submission.id FROM ParticipantSubmissionVote v" + " INNER JOIN Submission s"
            + " ON v.submission.id = s.id"
            + " WHERE s.participant.id != :participantId AND v.participant.id = :participantId)")
    List<Submission> getSubmissionNotUpvotedYetByUser(Long challengeId, Long participantId);
}
