package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.LeagueSecrets;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.exception.NotCreatorOfException;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * The main place for operation where the subject is an invitation. This means the fetching of the hidden identifier of
 * a {@link at.ac.tuwien.ase.groupphase.backend.entity.League} in order to construct the invitation link or the join
 * functionality.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitation")
public class InvitationService {

    /* todo delete comment
    INSERT INTO LEAGUE (id, challenge_duration, game_mode, region, hidden_identifier)
VALUES (1, 30, 1, 2, 'd5c30be9-c2a8-4038-9ff9-c807e40cf258');

     */
    private final Logger logger = LoggerFactory.getLogger(InvitationService.class);

    private final UserRepository userRepository;
    private final LeagueRepository leagueRepository;

    @GetMapping("/hidden-identifier/{leagueId}")
    @ResponseStatus(HttpStatus.OK)
    public LeagueSecrets getHiddenIdentifier(@NotNull @PathVariable final long leagueId,
            @NotNull @Autowired final Principal principal) {
        logger.trace("getHiddenIdentifier({},{})", leagueId, principal.getName());
        if (this.userRepository.isCreatorOfLeague(principal.getName(), leagueId)) {
            logger.info(
                    "Unauthorized access to the hidden identifier of the league with the id '{}' from the user '{}', rejecting",
                    leagueId, principal.getName());
            throw new NotCreatorOfException(principal.getName(), leagueId);
        }
        final var league = this.leagueRepository.findById(leagueId);
        if (league.isEmpty()) {
            logger.warn("League with id '{}' does not seems to exist, even it was the case during creator validation",
                    leagueId);
        }
        return new LeagueSecrets(league.map(League::getHiddenIdentifier).orElse(null));
    }

}
