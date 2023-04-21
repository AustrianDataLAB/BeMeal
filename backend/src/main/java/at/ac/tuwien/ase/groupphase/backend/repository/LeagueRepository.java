package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface LeagueRepository extends CrudRepository<League, Long> {
    List<League> findLeaguesByParticipantsIn(Set<Participant> participants);

    League findLeagueByHiddenIdentifier(UUID hiddenIdentifier);

    @Query("select distinct l from League l left join l.challenges c group by l having max(c.endDate) < :endExclusive")
    Stream<League> findLeaguesWithNoValidChallengeAt(LocalDate endExclusive);
}
