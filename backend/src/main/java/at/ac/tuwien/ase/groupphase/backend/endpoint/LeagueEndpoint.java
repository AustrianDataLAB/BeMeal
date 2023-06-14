package at.ac.tuwien.ase.groupphase.backend.endpoint;

import at.ac.tuwien.ase.groupphase.backend.dto.ChallengeInfoDto;
import at.ac.tuwien.ase.groupphase.backend.dto.LeaderboardDto;
import at.ac.tuwien.ase.groupphase.backend.dto.LeagueDto;
import at.ac.tuwien.ase.groupphase.backend.dto.WinningSubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.mapper.LeagueMapper;
import at.ac.tuwien.ase.groupphase.backend.service.ChallengeGenerationService;
import at.ac.tuwien.ase.groupphase.backend.service.LeagueService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/league")
@RequiredArgsConstructor
@Slf4j
public class LeagueEndpoint {

    private final LeagueMapper leagueMapper;
    private final LeagueService leagueService;

    private final ChallengeGenerationService challengeGenerationService;

    @PostMapping("/create-league")
    @SecurityRequirement(name = "bearerToken")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLeague(@NotNull @RequestBody final LeagueDto leagueDto) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        League league = this.leagueMapper.leagueDtoToLeague(leagueDto);
        this.leagueService.createLeague(user, league);
    }

    // e.g. http://localhost:4200/league/join/f172c3e8-4a27-4cb4-bd16-ee3e97bf5583
    @GetMapping("/hidden-identifier/{hiddenIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public ResponseEntity<LeagueDto> getLeagueByHiddenIdentifier(@NotNull @PathVariable final String hiddenIdentifier) {
        LeagueDto leagueDto = leagueService.getLeagueWithHiddenIdentifier(UUID.fromString(hiddenIdentifier));
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

    @GetMapping("/challenge/{id}")
    @SecurityRequirement(name = "bearerToken")
    @ResponseStatus(HttpStatus.OK)
    public ChallengeInfoDto getChallengeForLeague(@NotNull @PathVariable final Long id) {
        return this.leagueService.getChallengeForLeague(id);
        // try {
        // return this.leagueService.getChallengeForLeague(id);
        // } catch (NoChallengeException e) {
        // throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        // } catch (NoSuchElementException e) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        // }

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public ResponseEntity<LeagueDto> getLeagueById(@NotNull @PathVariable final Long id) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<League> leagues = this.leagueService.getLeagues(user);
        Optional<League> leagueOptional = leagues.stream().filter(league -> league.getId().equals(id)).findFirst();
        if (leagueOptional.isPresent()) {
            League league = leagueOptional.get();
            return ResponseEntity.ok(this.leagueMapper.leagueToLeagueDto(league));
        } else {
            throw new NoSuchElementException("User not in league");
            // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not in league");
        }
    }

    @GetMapping("/{leagueId}/leaderboard")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public List<LeaderboardDto> getLeaderboardByLeagueId(@NotNull @PathVariable final Long leagueId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return leagueService.getLeaderboardOfLeague(leagueId, username);
    }

    @GetMapping("/{leagueId}/winningSubmissions")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public List<WinningSubmissionDto> getLastWinningSubmissions(@NotNull @PathVariable final Long leagueId) {
        return leagueService.getLastWinningSubmissions(leagueId);
    }

    /**
     * Generate new challenges for leagues. This endpoint is for debugging purposes only. Only gamemasters may call this
     * endpoint.
     *
     * @param force
     *            if 'true' all challenges will be newly generated, if 'false' only for leagues with expired or no
     *            challenges a new challenge will be generated
     */
    @PutMapping("/challenges/{force}")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerToken")
    // @PreAuthorize("hasRole('GAMEMASTER')")
    public void generateChallenges(@PathVariable final boolean force) {
        log.info("Generate new challenges manually");
        if (force) {
            this.challengeGenerationService.generateAllNewChallenges();
        } else {
            this.challengeGenerationService.generateForExpiredChallenges();
        }
    }
}
