package at.ac.tuwien.ase.groupphase.backend.endpoint;

import at.ac.tuwien.ase.groupphase.backend.dto.JoinLeagueDto;
import at.ac.tuwien.ase.groupphase.backend.dto.LeagueDto;
import at.ac.tuwien.ase.groupphase.backend.dto.LeagueSecretsDto;
import at.ac.tuwien.ase.groupphase.backend.exception.NotCreatorOfException;
import at.ac.tuwien.ase.groupphase.backend.service.LeagueService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

/**
 * The main place for operation where the subject is an invitation. This means the fetching of the hidden identifier of
 * a {@link at.ac.tuwien.ase.groupphase.backend.entity.League} in order to construct the invitation link or the join
 * functionality.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation")
public class InvitationEndpoint {

    private final Logger logger = LoggerFactory.getLogger(InvitationEndpoint.class);
    @Autowired
    private final LeagueService leagueService;

    @GetMapping("/hidden-identifier/{leagueId}")
    @ResponseStatus(HttpStatus.OK)
    public LeagueSecretsDto getHiddenIdentifier(@NotNull @PathVariable final long leagueId,
            @NotNull @Autowired final Principal principal) {
        logger.trace("getHiddenIdentifier({},{})", leagueId, principal.getName());
        if (!this.leagueService.isUserCreatorOfLeague(principal.getName(), leagueId)) {
            logger.info(
                    "Unauthorized access to the hidden identifier of the league with the id '{}' from the user '{}', rejecting",
                    leagueId, principal.getName());
            throw new NotCreatorOfException(principal.getName(), leagueId);
        }

        return this.leagueService.getLeagueSecretsWithLeagueId(leagueId);
    }

    @GetMapping("/join-league/{hiddenIdentifier}")
    @SecurityRequirement(name = "bearerToken")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> joinLeague(@NotNull @PathVariable final String hiddenIdentifier) {
        LeagueDto leagueDto = leagueService.getLeagueWithHiddenIdentifier(UUID.fromString(hiddenIdentifier));
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.leagueService.joinLeague(user, leagueDto.id());
        return ResponseEntity.noContent().build();
    }

}
