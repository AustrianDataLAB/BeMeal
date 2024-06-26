package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.LeagueDto;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.ParticipantSubmissionVote;
import at.ac.tuwien.ase.groupphase.backend.entity.Submission;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeagueMapper {

    private final LeagueRepository leagueRepository;

    public League leagueDtoToLeague(@NotNull final LeagueDto dto) {
        final var league = new League();
        league.setChallengeDuration(dto.challengeDuration());
        league.setGameMode(dto.gameMode());
        league.setName(dto.name());
        return league;

    }

    public LeagueDto leagueToLeagueDto(@NotNull final League league) {
        final var lastChallenge = this.leagueRepository.findLastEndedChallenge(league.getId(), LocalDate.now());
        final var lastWinners = lastChallenge.map(c -> {
            final var orderedSubmissions = c
                    .getSubmissions().stream().sorted(Comparator.comparingLong((Submission submission) -> submission
                            .getUpVotes().stream().filter(ParticipantSubmissionVote::isUpvote).count()).reversed())
                    .toList();
            final var maxUpVotes = orderedSubmissions.stream().findFirst()
                    .map(s -> s.getUpVotes().stream().filter(ParticipantSubmissionVote::isUpvote).count())
                    .orElse(Long.MAX_VALUE);
            return orderedSubmissions.stream()
                    .filter(s -> s.getUpVotes().stream().filter(ParticipantSubmissionVote::isUpvote)
                            .count() == maxUpVotes && !s.getUpVotes().isEmpty())
                    .map(s -> s.getParticipant().getUsername()).toList();
        });
        return new LeagueDto(league.getId(), league.getName(), league.getGameMode(), league.getChallengeDuration(),
                lastWinners.orElse(List.of()));
    }

    public List<LeagueDto> leagueListToLeagueDtoList(@NotNull final List<League> leagueList) {
        List<LeagueDto> ret = new ArrayList<>();
        for (League league : leagueList) {
            ret.add(leagueToLeagueDto(league));
        }
        return ret;
    }
}
