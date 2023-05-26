package at.ac.tuwien.ase.groupphase.backend.integrationtest;

import at.ac.tuwien.ase.groupphase.backend.entity.GameMode;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.mapper.LeagueMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.service.ChallengeGenerationService;
import at.ac.tuwien.ase.groupphase.backend.service.LeagueService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static util.Constants.*;

@SpringBootTest
@Transactional
@DirtiesContext
public class LeagueServiceTest {

    @InjectMocks
    private final LeagueService leagueService;

    private final LeagueMapper leagueMapper;

    private final ParticipantRepository participantRepository;
    private final LeagueRepository leagueRepository;

    @Autowired
    public LeagueServiceTest(LeagueService leagueService, LeagueMapper leagueMapper,
            ParticipantRepository participantRepository, LeagueRepository leagueRepository) {
        this.leagueService = leagueService;
        this.leagueMapper = leagueMapper;
        this.participantRepository = participantRepository;
        this.leagueRepository = leagueRepository;
    }

    @BeforeEach
    void beforeEach() {
        this.participantRepository.deleteAll();
        this.participantRepository.save(VALID_PARTICIPANT_1);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(VALID_USER_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        ChallengeGenerationService challengeGenerationService = Mockito.mock(ChallengeGenerationService.class);
        Mockito.when(challengeGenerationService.randomRecipe(GameMode.PICTURE_INGREDIENTS))
                .thenReturn(Optional.of(RECIPE1));
        ReflectionTestUtils.setField(this.leagueService, "challengeGenerationService", challengeGenerationService);

        this.leagueRepository.deleteAll();
    }

    @Test
    void test_createLeague() {
        assertEquals(0, StreamSupport.stream(this.leagueRepository.findAll().spliterator(), false).count());

        this.leagueService.createLeague(VALID_USER_USERNAME, LEAGUE1);

        assertEquals(1, StreamSupport.stream(this.leagueRepository.findAll().spliterator(), false).count());
        League result = this.leagueRepository.findAll().iterator().next();
        assertNotNull(result);
        assertAll(() -> assertEquals(LEAGUE1.getHiddenIdentifier(), result.getHiddenIdentifier()),
                () -> assertEquals(LEAGUE1.getGameMode(), result.getGameMode()),
                () -> assertEquals(LEAGUE1.getRegion(), result.getRegion()),
                () -> assertEquals(LEAGUE1.getName(), result.getName()),
                () -> assertEquals(LEAGUE1.getParticipants(), result.getParticipants()));
    }

}
