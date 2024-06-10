package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeGenerationService {

    private final ChallengeRepository challengeRepository;
    private final LeagueRepository leagueRepository;
    private final RecipeRepository recipeRepository;
    private final ParticipantService participantService;
    private final SubmissionService submissionService;

    private final Random random = new Random();

    @Setter
    private boolean failMode = true;

    @Scheduled(cron = "0 0 3 * * ?")
    public void generateChallenges() {
        log.info("Schedule generating new challenges when expired...");
        this.generateForExpiredChallenges();
        log.info("Done generating the new challenges by scheduler");
    }

    /**
     * Generate a new challenge for a given {@link League}. This does not take into consideration whether a challenge is
     * already running for this league or not. In case no recipe can be found, the old challenge remains.
     *
     * @param league
     *            the league to generate a new challenge for
     */
    @Transactional("rdbmsTxManager")
    public void generateNewChallenge(final League league) {
        log.info("Generate a new challenge for league {}", league.getName());
        final var gameMode = league.getGameMode();
        final var newRecipe = randomRecipe(gameMode);
        if (newRecipe.isEmpty()) {
            log.warn("Retrieved no new recipe for league {}, the old challenge remains", league.getName());
            return;
        }
        final var recipe = newRecipe.get();
        final var challenge = new Challenge();
        challenge.setRecipe(recipe.getRecipeId());
        challenge.setDescription(recipe.getDescription());
        challenge.setStartDate(LocalDate.now());
        challenge.setEndDate(LocalDate.now().plusDays(league.getChallengeDuration() - 1L));
        challenge.setLeague(league);

        this.challengeRepository.save(challenge);
        log.info("Generated a new challenge for league {}", league.getName());

        log.info("Updating wins");
        var dateNow = LocalDate.now();
        var opt = leagueRepository.findLastEndedChallenge(league.getId(), dateNow);

        if (opt.isEmpty()) {
            return;
        }

        Challenge oldChallenge = opt.get();

        log.info("Expired challenge id is {}", oldChallenge.getId());

        List<SubmissionWithUpvotes> winningSubmissions = submissionService
                .getWinningSubmissionForChallange(oldChallenge.getId());

        log.info("Winning submissions: {}", winningSubmissions.size());

        for (SubmissionWithUpvotes s : winningSubmissions) {
            log.info("Winning submission participant id {}", s.getSubmission().getParticipant().getId());
            participantService.increaseWinsOfParticipant(s.getSubmission().getParticipant().getId(), league.getId());
        }

    }

    /**
     * Generate new challenges for all leagues which either have no or only expired challenges. Also updates wins of
     * Participants, if they have won the previous expired challenge
     */
    @Transactional("rdbmsTxManager")
    public void generateForExpiredChallenges() {
        var dateNow = LocalDate.now();
        log.info("Generate new challenges for leagues with no valid challenge");
        Stream.concat(this.leagueRepository.findLeaguesWithNoValidChallengeAt(dateNow),
                this.leagueRepository.findLeaguesWithNoChallenges()).forEach(this::generateNewChallenge);
        log.info("Done generating new challenges");
    }

    /**
     * Generate new challenges for all leagues ignoring the current state of their challenges.
     */
    @Transactional("rdbmsTxManager")
    public void generateAllNewChallenges() {
        log.info("Generate new challenges for all leagues");
        this.leagueRepository.findAll().forEach(this::generateNewChallenge);
        log.info("Done generating new challenges");
    }

    /**
     * Fetch a random recipe for the given specifications. If no recipe can be found, this method returns
     * {@link Optional#empty()}.
     *
     * @param gameMode
     *            the gameMode required for the recipe
     *
     * @return the fetched recipe or empty if none can be found
     */
    @Transactional("neo4jTxManager")
    public Optional<Recipe> randomRecipe(final GameMode gameMode) {
        if (!this.failMode) {
            final var recipe = new Recipe(UUID.randomUUID().toString(), "Variation No. " + random.nextInt(73),
                    random.nextInt(30), random.nextInt(30), "This is some description", "baum", null);
            return Optional.of(recipe);
        }
        if (GameMode.INGREDIENTS.equals(gameMode)) {
            return this.recipeRepository.findAnyRecipe();
        }
        return this.recipeRepository.findAnyRecipeWithPicture();
    }
}
