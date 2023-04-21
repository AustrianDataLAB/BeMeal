package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import at.ac.tuwien.ase.groupphase.backend.entity.GameMode;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeGenerationService {

    private final ChallengeRepository challengeRepository;

    private final Random random = new Random();

    /**
     * Generate a new challenge for a given {@link League}. This does not take into consideration whether a challenge is
     * already running for this league or not. In case no recipe can be found, the old challenge remains.
     *
     * @param league
     *            the league to generate a new challenge for
     */
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
        challenge.setRecipe(recipe.getId());
        challenge.setDescription(recipe.getName() + " with " + (GameMode.PICTURE.equals(gameMode) ? "Picture only"
                : (GameMode.INGREDIENTS.equals(gameMode) ? "Ingredients only" : " full Recipe")));
        challenge.setStartDate(LocalDate.now());
        challenge.setEndDate(LocalDate.now().plusDays(league.getChallengeDuration() - 1L));
        challenge.setLeague(league);

        this.challengeRepository.save(challenge);
        log.info("Generated a new challenge for league {}", league.getName());

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
    public Optional<Recipe> randomRecipe(final GameMode gameMode) {
        // todo: this is only a dummy, a connection to the graph database is required
        final var recipe = new Recipe(UUID.randomUUID(), "Variation No. " + random.nextInt(73), UUID.randomUUID());
        if (GameMode.PICTURE.equals(gameMode) || GameMode.PICTURE_INGREDIENTS.equals(gameMode)) {
            return random.nextDouble() < 0.3 ? Optional.of(recipe) : Optional.empty();
        }
        return random.nextDouble() < 0.8 ? Optional.of(recipe) : Optional.empty();
    }
}
