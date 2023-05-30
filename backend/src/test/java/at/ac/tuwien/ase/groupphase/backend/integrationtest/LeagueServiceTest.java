package at.ac.tuwien.ase.groupphase.backend.integrationtest;

import at.ac.tuwien.ase.groupphase.backend.dto.ChallengeInfoDto;
import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.mapper.IngredientMapper;
import at.ac.tuwien.ase.groupphase.backend.mapper.LeagueMapper;
import at.ac.tuwien.ase.groupphase.backend.mapper.RecipeMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.service.ChallengeGenerationService;
import at.ac.tuwien.ase.groupphase.backend.service.LeagueService;
import at.ac.tuwien.ase.groupphase.backend.service.RecipeService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static util.Constants.*;

@SpringBootTest
@Transactional
@DirtiesContext
public class LeagueServiceTest {

    @InjectMocks
    private final LeagueService leagueService;
    private final ChallengeGenerationService challengeGenerationService;

    private final LeagueMapper leagueMapper;
    private final IngredientMapper ingredientMapper = new IngredientMapper();
    private final RecipeMapper recipeMapper = new RecipeMapper();

    private final ParticipantRepository participantRepository;
    private final LeagueRepository leagueRepository;
    private final ChallengeRepository challengeRepository;

    private final static League LEAGUE1 = new League(null, UUID.randomUUID(), GameMode.PICTURE_INGREDIENTS,
            Region.VORARLBERG, 7, "League 1", new ArrayList<>(), new ArrayList<>());

    private final static Recipe RECIPE1 = new Recipe("99", "recipe1", 10, 11, "description", "skill level",
            "7ee65cc3-b719-4bf6-872e-b253dace5ff1", List.of(new Ingredient(UUID.randomUUID(), "ayyLmao")),
            new ArrayList<>());

    private final static Participant VALID_PARTICIPANT_1 = new Participant(VALID_USER_ID, VALID_USER_EMAIL,
            VALID_USER_PASSWORD_BYTES, VALID_USER_USERNAME, Boolean.FALSE, new ArrayList<>(), VALID_USER_POSTAL_CODE,
            VALID_WINS, VALID_USER_REGION, VALID_LOCALDATETIME, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>());

    private static final Challenge CHALLENGE1 = new Challenge(1L, "challenge description",
            LocalDateTime.now().toLocalDate(), LocalDateTime.now().plusDays(7).toLocalDate(), "Recipe",
            new ArrayList<>(), LEAGUE1);

    @Autowired
    public LeagueServiceTest(LeagueService leagueService, ChallengeGenerationService challengeGenerationService,
            LeagueMapper leagueMapper, ParticipantRepository participantRepository, LeagueRepository leagueRepository,
            ChallengeRepository challengeRepository) {
        this.leagueService = leagueService;
        this.challengeGenerationService = challengeGenerationService;
        this.leagueMapper = leagueMapper;
        this.participantRepository = participantRepository;
        this.leagueRepository = leagueRepository;
        this.challengeRepository = challengeRepository;
    }

    @BeforeEach
    void beforeEach() {
        assertEquals(0, StreamSupport.stream(this.participantRepository.findAll().spliterator(), false).count());
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

        this.challengeGenerationService.setFailMode(false);

        this.challengeRepository.deleteAll();
        this.leagueRepository.deleteAll();
    }

    @AfterEach
    void afterEach() {
        this.challengeRepository.deleteAll();
        this.participantRepository.deleteAll();
        this.leagueRepository.deleteAll();
    }

    @Test
    void createLeague() {
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

    @Test
    void getChallengeForLeague() {
        assertEquals(0, StreamSupport.stream(this.leagueRepository.findAll().spliterator(), false).count());

        this.leagueService.createLeague(VALID_USER_USERNAME, LEAGUE1);

        assertEquals(1, StreamSupport.stream(this.leagueRepository.findAll().spliterator(), false).count());
        League league = this.leagueRepository.findAll().iterator().next();

        this.challengeGenerationService.generateNewChallenge(league);
        assertEquals(1, StreamSupport.stream(this.challengeRepository.findAll().spliterator(), false).count());
        Challenge c = this.challengeRepository.findAll().iterator().next();

        RecipeService recipeService = Mockito.mock(RecipeService.class);
        Mockito.when(recipeService.getRecipeById(c.getRecipe()))
                .thenReturn(this.recipeMapper.recipeToRecipeDto(RECIPE1));
        ReflectionTestUtils.setField(this.leagueService, "recipeService", recipeService);

        ChallengeInfoDto actual = this.leagueService.getChallengeForLeague(league.getId());

        assertEquals(c.getId(), actual.getChallengeId());
        assertEquals(RECIPE1.getDescription(), actual.getDescription());
        assertEquals(c.getEndDate(), actual.getEndDate());
        assertEquals(RECIPE1.getIngredients().stream().map(ingredientMapper::ingredientToIngredientDto)
                .collect(Collectors.toList()), actual.getIngredients());
        assertEquals(this.recipeMapper.uuidToBase64Converter(RECIPE1.getPictureUUID()), actual.getPicture());
    }

}
