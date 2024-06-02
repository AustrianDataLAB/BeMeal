package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findByUsername(String username);

    @Query("SELECT l.participants from League l WHERE l.id = :leagueId")
    List<Participant> getParticipantRankingForLeague(Long leagueId);

}
