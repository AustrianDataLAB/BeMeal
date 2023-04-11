package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.LeagueDto;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class LeagueMapper {

    public League leagueDtoToLeague(@NotNull final LeagueDto dto) {
        final var league = new League();
        league.setChallengeDuration(dto.challengeDuration());
        league.setRegion(dto.region());
        league.setGameMode(dto.gameMode());
        return league;

    }
}
