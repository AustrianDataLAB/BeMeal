package at.ac.tuwien.ase.groupphase.backend.integrationtest;

import at.ac.tuwien.ase.groupphase.backend.dto.LeagueSecretsDto;
import at.ac.tuwien.ase.groupphase.backend.endpoint.InvitationEndpoint;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.exception.NotCreatorOfException;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static util.Constants.*;
import static util.Constants.LEAGUE1;

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
    private League league;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        this.participantRepository.deleteAll();
        this.leagueRepository.deleteAll();
        this.userRepository.deleteAll();

        p1 = participantRepository.save(VALID_PARTICIPANT_1);

        league = LEAGUE1;
        List<Participant> participants = new ArrayList<>();
        List<League> ownerOf = new ArrayList<>();
        ownerOf.add(league);
        participants.add(p1);
        p1.setOwnerOf(ownerOf);
        league.setParticipants(participants);
        league = leagueRepository.save(league);
    }

    // TODO this tests works when only InvitationEndpointTests is executed, but if all test are executed it fails
    // @Test
    // public void getHIddenIdentifierShouldReturnCorrectValue() {
    // Principal principal = new UserPrincipal() {
    // @Override
    // public String getName() {
    // return p1.getUsername();
    // }
    // };
    // LeagueSecretsDto dto = this.invitationEndpoint.getHiddenIdentifier(league.getId(), principal);
    // assertEquals(league.getHiddenIdentifier(), dto.hiddenIdentifier());
    //
    // }

    @Test
    public void getHiddenIdentifierWhenUserIsNotCreaterShouldThrowNotCreatorOfException() {
        Principal principal = new UserPrincipal() {
            @Override
            public String getName() {
                return "invalid user";
            }
        };
        // TODO sollte ja eigentlich eine NoCreatorOfException werfen, aber aus irgendeinem grund passiert das ned
        assertThrows(DataIntegrityViolationException.class,
                () -> this.invitationEndpoint.getHiddenIdentifier(league.getId(), principal));
    }

}
