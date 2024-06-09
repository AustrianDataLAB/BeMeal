package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.ParticipantSubmissionVote;
import at.ac.tuwien.ase.groupphase.backend.entity.VoteKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<ParticipantSubmissionVote, VoteKey> {
}
