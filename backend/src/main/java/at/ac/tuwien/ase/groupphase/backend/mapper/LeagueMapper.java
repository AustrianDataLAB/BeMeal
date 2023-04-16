package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.LeagueDto;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeagueMapper {

    public League leagueDtoToLeague(@NotNull final LeagueDto dto) {
        final var league = new League();
        league.setChallengeDuration(dto.challengeDuration());
        league.setRegion(dto.region());
        league.setGameMode(dto.gameMode());
        league.setName(dto.name());
        return league;

    }

    public LeagueDto leagueToLeagueDto(@NotNull final League league) {
        return new LeagueDto(league.getName(), league.getGameMode(), league.getChallengeDuration(), league.getRegion());
    }

    public List<LeagueDto> leagueListToLeagueDtoList(@NotNull final List<League> leagueList) {
        List<LeagueDto> ret = new ArrayList<>();
        for (League league : leagueList) {
            ret.add(leagueToLeagueDto(league));
        }
        return ret;
    }
}
