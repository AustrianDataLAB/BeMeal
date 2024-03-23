package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    Participant findByUsername(String username);

    @Query("SELECT l.participants from League l WHERE l.id = :leagueId")
    List<Participant> getParticipantRankingForLeague(Long leagueId);

}
