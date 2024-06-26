package at.ac.tuwien.ase.groupphase.backend.integrationtest;

import at.ac.tuwien.ase.groupphase.backend.dto.LeagueSecretsDto;
import at.ac.tuwien.ase.groupphase.backend.endpoint.InvitationEndpoint;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.exception.AlreadyJoinedException;
import at.ac.tuwien.ase.groupphase.backend.exception.NotCreatorOfException;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static util.Constants.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InvitationEndpointTest {

    @Autowired
    private InvitationEndpoint invitationEndpoint;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private LeagueRepository leagueRepository;

    private Participant p1;
    private Participant p2;
    private League league;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        p1 = participantRepository.save(VALID_PARTICIPANT_1);
        p2 = participantRepository.save(VALID_PARTICIPANT_2);

        league = LEAGUE1;
        List<Participant> participants = new ArrayList<>();
        List<League> ownerOf = new ArrayList<>();
        participants.add(p1);
        league.setParticipants(participants);
        league = leagueRepository.save(league);
        ownerOf.add(league);
        p1.setOwnerOf(ownerOf);
        participantRepository.save(p1);

    }

    @Test
    public void getHIddenIdentifierShouldReturnCorrectValue() {
        Principal principal = new UserPrincipal() {
            @Override
            public String getName() {
                return p1.getUsername();
            }
        };
        LeagueSecretsDto dto = this.invitationEndpoint.getHiddenIdentifier(league.getId(), false, principal);
        assertEquals(league.getHiddenIdentifier(), dto.hiddenIdentifier());
        // System.out.println("hello");

    }

    @Test
    public void getHiddenIdentifierWhenUserIsNotCreaterShouldThrowNotCreatorOfException() {
        Principal principal = new UserPrincipal() {
            @Override
            public String getName() {
                return "invalid user";
            }
        };
        assertThrows(NotCreatorOfException.class,
                () -> this.invitationEndpoint.getHiddenIdentifier(league.getId(), false, principal));
    }

    @Test
    public void joinLeagueUserShouldJoinLeague() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(p2.getUsername());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ResponseEntity<?> reponse = this.invitationEndpoint.joinLeague(league.getHiddenIdentifier().toString());
        assertEquals(204, reponse.getStatusCode().value());

    }

    @Test
    void userCannotJoinMultipleTimes() {
        final var authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(p2.getUsername());
        final var securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        this.invitationEndpoint.joinLeague(league.getHiddenIdentifier().toString());
        final var hiddenIdentifier = league.getHiddenIdentifier().toString();

        assertThrows(AlreadyJoinedException.class, () -> this.invitationEndpoint.joinLeague(hiddenIdentifier));
        assertEquals(1, this.leagueRepository.findById(league.getId()).get().getParticipants().stream()
                .filter(p -> p.getId().equals(p2.getId())).count());
    }

}
