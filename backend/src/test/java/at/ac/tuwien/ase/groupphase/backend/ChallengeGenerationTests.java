package at.ac.tuwien.ase.groupphase.backend;

import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import at.ac.tuwien.ase.groupphase.backend.entity.GameMode;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Region;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.service.ChallengeGenerationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChallengeGenerationTests {

    private final LeagueRepository leagueRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeGenerationService challengeGenerationService;
    private final EntityManager entityManager;

    @Autowired
    ChallengeGenerationTests(final LeagueRepository leagueRepository, final ChallengeRepository challengeRepository,
            final ChallengeGenerationService challengeGenerationService, final EntityManager entityManager) {
        this.leagueRepository = leagueRepository;
        this.challengeRepository = challengeRepository;
        this.challengeGenerationService = challengeGenerationService;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setup() {
        this.challengeGenerationService.setFailMode(false);
    }

    @Test
    @Transactional
    void leagueWithoutChallengeGenerates() {
        final var league = new League();
        league.setName("t1");
        league.setRegion(Region.BURGENLAND);
        league.setGameMode(GameMode.PICTURE_INGREDIENTS);
        league.setChallengeDuration(4);
        league.setHiddenIdentifier(UUID.randomUUID());

        final var persisted = this.leagueRepository.save(league);

        assertNull(persisted.getChallenges());
        this.challengeGenerationService.generateForExpiredChallenges();
        this.flush();
        assertFalse(this.leagueRepository.findById(persisted.getId()).get().getChallenges().isEmpty());
    }

    @Test
    @Transactional
    void leagueWithExpiredChallengeGenerates() {
        final var league = new League();
        league.setName("t1");
        league.setRegion(Region.BURGENLAND);
        league.setGameMode(GameMode.PICTURE_INGREDIENTS);
        league.setChallengeDuration(4);
        league.setHiddenIdentifier(UUID.randomUUID());

        this.leagueRepository.save(league);

        final var challenge = new Challenge();
        challenge.setLeague(league);
        challenge.setDescription("c0");
        final var now = LocalDate.now();
        challenge.setStartDate(now.minusDays(3));
        challenge.setEndDate(now.minusDays(1));
        challenge.setRecipe(UUID.randomUUID());

        final var persistedChallenge = this.challengeRepository.save(challenge);
        this.challengeGenerationService.generateForExpiredChallenges();

        this.flush();

        final var challenges = this.leagueRepository.findById(league.getId()).get().getChallenges();
        assertEquals(2, challenges.size());
        final var otherChallenge = challenges.stream()
                .filter(c -> !Objects.equals(c.getId(), persistedChallenge.getId())).findAny().get();
        assertEquals(now, otherChallenge.getStartDate());
        assertEquals(now.plusDays(3), otherChallenge.getEndDate());
    }

    @Test
    @Transactional
    void leagueWithValidChallengeGeneratesNot() {
        final var league = new League();
        league.setName("t1");
        league.setRegion(Region.BURGENLAND);
        league.setGameMode(GameMode.PICTURE_INGREDIENTS);
        league.setChallengeDuration(4);
        league.setHiddenIdentifier(UUID.randomUUID());

        this.leagueRepository.save(league);

        final var challenge = new Challenge();
        challenge.setLeague(league);
        challenge.setDescription("c0");
        final var now = LocalDate.now();
        challenge.setStartDate(now.minusDays(3));
        challenge.setEndDate(now);
        challenge.setRecipe(UUID.randomUUID());

        this.challengeRepository.save(challenge);
        this.challengeGenerationService.generateForExpiredChallenges();

        this.flush();

        final var challenges = this.leagueRepository.findById(league.getId()).get().getChallenges();
        assertEquals(1, challenges.size());
    }

    @Test
    @Transactional
    void forceGeneratesAll() {
        final var league = new League();
        league.setName("t1");
        league.setRegion(Region.BURGENLAND);
        league.setGameMode(GameMode.PICTURE_INGREDIENTS);
        league.setChallengeDuration(4);
        league.setHiddenIdentifier(UUID.randomUUID());

        this.leagueRepository.save(league);

        final var challenge = new Challenge();
        challenge.setLeague(league);
        challenge.setDescription("c0");
        final var now = LocalDate.now();
        challenge.setStartDate(now.minusDays(3));
        challenge.setEndDate(now);
        challenge.setRecipe(UUID.randomUUID());

        final var persistedChallenge = this.challengeRepository.save(challenge);
        this.challengeGenerationService.generateAllNewChallenges();

        this.flush();

        final var challenges = this.leagueRepository.findById(league.getId()).get().getChallenges();
        assertEquals(2, challenges.size());
        final var otherChallenge = challenges.stream()
                .filter(c -> !Objects.equals(c.getId(), persistedChallenge.getId())).findAny().get();
        assertEquals(now, otherChallenge.getStartDate());
        assertEquals(now.plusDays(3), otherChallenge.getEndDate());
    }

    private void flush() {
        this.entityManager.flush();
        this.entityManager.clear();
    }
}
