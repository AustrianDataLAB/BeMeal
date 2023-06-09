package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.*;
import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.exception.AlreadyJoinedException;
import at.ac.tuwien.ase.groupphase.backend.exception.NoChallengeException;
import at.ac.tuwien.ase.groupphase.backend.mapper.LeagueMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LeagueService {
    private final UserRepository userRepository;
    private final LeagueRepository leagueRepository;
    private final ChallengeRepository challengeRepository;
    private final ParticipantRepository participantRepository;
    private final ChallengeGenerationService challengeGenerationService;

    private final RecipeService recipeService;
    private final LeagueMapper leagueMapper;
    private final Logger logger = LoggerFactory.getLogger(LeagueService.class);

    /*
     * @Autowired
     *
     * @NotNull public LeagueService(UserRepository userRepository, LeagueRepository leagueRepository,
     * ChallengeRepository challengeRepository, ChallengeGenerationService challengeGenerationService, RecipeService
     * recipeService, LeagueMapper leagueMapper) { this.userRepository = userRepository; this.leagueRepository =
     * leagueRepository; this.challengeRepository = challengeRepository; this.challengeGenerationService =
     * challengeGenerationService; this.recipeService = recipeService; this.leagueMapper = leagueMapper; }
     */

    @Transactional
    public LeagueSecretsDto getLeagueSecretsWithLeagueId(Long id, boolean refresh) {
        var league = leagueRepository.findById(id);

        if (league.isEmpty()) {
            logger.warn("League with id '{}' does not seems to exist, even it was the case during creator validation",
                    id);
            throw new NoSuchElementException("Could not find league");
        }

        if (refresh) {
            // create new hidden identifier with each request
            league.get().setHiddenIdentifier(UUID.randomUUID());
        }
        return new LeagueSecretsDto(league.map(League::getHiddenIdentifier).orElse(null));
    }

    @Transactional
    public ChallengeInfoDto getChallengeForLeague(Long id) {
        League league = this.leagueRepository.findById(id).orElseThrow();
        // if (league.getChallenges().isEmpty()) {
        // throw new NoChallengeException();
        // }
        Challenge challenge = this.challengeRepository.getLatestChallenge(league.getId());
        if (challenge == null) {
            throw new NoChallengeException();
        }

        RecipeDto recipe = this.recipeService.getRecipeById(challenge.getRecipe());

        ChallengeInfoDto dto = new ChallengeInfoDto();
        dto.setDescription(recipe.getDescription());
        dto.setName(recipe.getName());
        dto.setEndDate(challenge.getEndDate());
        dto.setChallengeId(challenge.getId());

        GameMode gameMode = league.getGameMode();

        if (gameMode == GameMode.PICTURE || gameMode == GameMode.PICTURE_INGREDIENTS) {
            dto.setPicture(recipe.getPicture());
        }
        if (gameMode != GameMode.PICTURE) {
            dto.setIngredients(recipe.getIngredients());
        }
        return dto;
    }

    public void createLeague(String username, League league) {
        Participant user = (Participant) this.userRepository.findByUsername(username);
        List<Participant> participantList = new ArrayList<>();
        participantList.add(user);
        league.setParticipants(participantList);
        League l = this.leagueRepository.save(league);
        List<League> owned = user.getOwnerOf();
        owned.add(l);
        user.setOwnerOf(owned);
        this.userRepository.save(user);
        // create challenge for new league:
        // this.challengeGenerationService.generateForExpiredChallenges();
    }

    /**
     * Adds a participant to a league
     *
     * @param username
     *            username of the participant to be added to the league
     * @param leagueId
     *            id of the league
     */
    public void joinLeague(String username, Long leagueId) {
        League league = this.leagueRepository.findById(leagueId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid league ID"));
        List<Participant> participantList = league.getParticipants();
        Participant user = (Participant) this.userRepository.findByUsername(username);
        if (participantList.stream().anyMatch(p -> p.getId().equals(user.getId()))) {
            throw new AlreadyJoinedException();
        }
        participantList.add(user);
        league.setParticipants(participantList);
        this.leagueRepository.save(league);
    }

    // important: regional leagues have to exist
    public void joinRegionalLeague(String username, Region region) {
        String leagueName = modifyString(region.toString()) + " League";
        System.out.println(leagueName);
        League league = this.leagueRepository.findLeagueByName(leagueName);
        if (league == null) {
            throw new IllegalArgumentException("could not find regional league");
        }

        List<Participant> participantList = league.getParticipants();
        Participant user = (Participant) this.userRepository.findByUsername(username);
        participantList.add(user);
        league.setParticipants(participantList);
        this.leagueRepository.save(league);
    }

    public List<League> getLeagues(String username) {
        Participant user = (Participant) this.userRepository.findByUsername(username);
        List<Participant> participantList = new ArrayList<>();
        participantList.add(user);
        return this.leagueRepository.findLeaguesByParticipantsIn(new HashSet<>(participantList));
    }

    public LeagueDto getLeagueWithHiddenIdentifier(UUID hiddenIdentifier) {
        League league = this.leagueRepository.findLeagueByHiddenIdentifier(hiddenIdentifier);

        if (league == null) {
            throw new NoSuchElementException("Could not find league with hidden identifier " + hiddenIdentifier);
        }

        return leagueMapper.leagueToLeagueDto(league);
    }

    public boolean isUserCreatorOfLeague(String username, long leagueId) {
        return this.userRepository.isCreatorOfLeague(username, leagueId);
    }

    public List<LeaderboardDto> getLeaderboardOfLeague(Long leagueId) {
        logger.trace("Constructing leaderboard with getLeaderboardOfLeague({})", leagueId);
        List<LeaderboardDto> leaderboard = new ArrayList<>();
        for (Participant p : participantRepository.getParticipantRankingForLeague(leagueId)) {
            logger.debug("Got Participant for league with id {}: {Username: {}, Wins: {}}", leagueId, p.getUsername(),
                    p.getWins());
            LeaderboardDto leaderboardDto = new LeaderboardDto(p.getUsername(), p.getWins());
            leaderboard.add(leaderboardDto);
        }

        // Sort the leaderboard based on points in descending order
        Collections.sort(leaderboard);

        int ranking = 1;
        if (leaderboard.size() > 0) {
            leaderboard.get(0).setPosition(ranking);
        }

        // Update the position field based on the sorted order
        for (int i = 1; i < leaderboard.size(); i++) {
            // If two participants have the same amount of wins, their position should be the same
            // if not, the ranking is increased (= "lower ranking")
            if (!leaderboard.get(i - 1).getWins().equals(leaderboard.get(i).getWins())) {
                ranking++;
            }

            leaderboard.get(i).setPosition(ranking);
        }

        return leaderboard;
    }

    private String modifyString(String str) {
        str = str.replace("_", " ").toLowerCase(); // replace '_' with ' ' and convert to lowercase
        StringBuilder sb = new StringBuilder(str);
        boolean capitalizeNext = true;
        for (int i = 0; i < sb.length(); i++) {
            if (Character.isWhitespace(sb.charAt(i))) {
                capitalizeNext = true; // set flag to capitalize the next letter
            } else if (capitalizeNext) {
                sb.setCharAt(i, Character.toUpperCase(sb.charAt(i))); // capitalize current letter
                capitalizeNext = false; // reset flag
            }
        }
        return sb.toString();
    }
}
