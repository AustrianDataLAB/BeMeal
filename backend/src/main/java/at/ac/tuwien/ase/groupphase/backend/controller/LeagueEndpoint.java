package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.JoinLeagueDto;
import at.ac.tuwien.ase.groupphase.backend.dto.LeagueDto;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.mapper.LeagueMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.service.LeagueService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/league")
public class LeagueEndpoint {

    private final Logger logger = LoggerFactory.getLogger(LeagueEndpoint.class);
    private final LeagueMapper leagueMapper;
    private final LeagueService leagueService;

    private final LeagueRepository leagueRepository;

    @Autowired
    @NotNull
    public LeagueEndpoint(LeagueMapper leagueMapper, final LeagueService leagueService,
            LeagueRepository leagueRepository) {
        this.leagueMapper = leagueMapper;
        this.leagueService = leagueService;
        this.leagueRepository = leagueRepository;
    }

    @PostMapping("/create-league")
    @SecurityRequirement(name = "bearerToken")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLeague(@NotNull @RequestBody final LeagueDto leagueDto) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        League league = this.leagueMapper.leagueDtoToLeague(leagueDto);
        this.leagueService.createLeague(user, league);
    }

    @PostMapping("/join-league")
    @SecurityRequirement(name = "bearerToken")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> joinLeague(@NotNull @RequestBody final JoinLeagueDto joinLeagueDto) {
        League league = this.leagueRepository
                .findLeagueByHiddenIdentifier(UUID.fromString(joinLeagueDto.hiddenIdentifier()));
        // league does not exist
        if (league == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.leagueService.joinLeague(user, joinLeagueDto.leagueId());
        return ResponseEntity.noContent().build();

    }

    // e.g. http://localhost:4200/league/join/f172c3e8-4a27-4cb4-bd16-ee3e97bf5583
    @GetMapping("/hidden-identifier/{hiddenIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public ResponseEntity<LeagueDto> getLeagueByHiddenIdentifier(@NotNull @PathVariable final String hiddenIdentifier) {
        UUID hi = UUID.fromString(hiddenIdentifier);
        League league = this.leagueRepository.findLeagueByHiddenIdentifier(hi);
        // league does not exist
        if (league == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        // league exists
        LeagueDto leagueDto = this.leagueMapper.leagueToLeagueDto(league);
        return ResponseEntity.ok(leagueDto);
    }

    @GetMapping("/leagues")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public List<LeagueDto> getLeagues() {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<League> leagues = this.leagueService.getLeagues(user);
        return this.leagueMapper.leagueListToLeagueDtoList(leagues);
    }
}
