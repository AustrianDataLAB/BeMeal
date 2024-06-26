package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface LeagueRepository extends JpaRepository<League, Long> {
    List<League> findLeaguesByParticipantsIn(Set<Participant> participants);

    // for regional leagues, name is: State league, e.g. Vorarlberg League, Vienna League, Upper Austria League
    League findLeagueByName(String name);

    League findLeagueByHiddenIdentifier(UUID hiddenIdentifier);

    @Query("select distinct l from League l left join l.challenges c group by l having max(c.endDate) < :endExclusive")
    Stream<League> findLeaguesWithNoValidChallengeAt(LocalDate endExclusive);

    @Query("SELECT DISTINCT c.league from Challenge c GROUP BY c HAVING MAX(c.endDate) < :endExclusive")
    List<League> findLeaguesWithExpiredChallenges(LocalDate endExclusive);

    @Query("select distinct l from League l left join l.challenges c where c is null")
    Stream<League> findLeaguesWithNoChallenges();

    @Query("select distinct c from League l left join l.challenges c where l.id = :leagueId and c.endDate < :endExclusive order by c.endDate desc limit 1")
    Optional<Challenge> findLastEndedChallenge(long leagueId, LocalDate endExclusive);
}
