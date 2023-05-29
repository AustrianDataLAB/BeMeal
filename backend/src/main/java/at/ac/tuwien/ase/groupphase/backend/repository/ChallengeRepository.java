package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ChallengeRepository extends CrudRepository<Challenge, Long> {

    @Query("SELECT c FROM Challenge c WHERE c.league.id = :leagueId ORDER BY c.id DESC LIMIT 1")
    Challenge getLatestChallenge(Long leagueId);

    @Query("SELECT c FROM Challenge c WHERE c.league.id = :leagueId AND c.endDate < getDate() ORDER BY c.endDate DESC LIMIT 1")
    Challenge getLastExpiredChallenge(Long leagueId);

}
