package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.ParticipantSubmissionVote;
import at.ac.tuwien.ase.groupphase.backend.entity.VoteKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends CrudRepository<ParticipantSubmissionVote, VoteKey> {
}
