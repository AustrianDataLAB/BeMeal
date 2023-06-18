package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.*;
import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.exception.AlreadyJoinedException;
import at.ac.tuwien.ase.groupphase.backend.exception.NoChallengeException;
import at.ac.tuwien.ase.groupphase.backend.exception.NoLatestChallengeException;
import at.ac.tuwien.ase.groupphase.backend.mapper.LeagueMapper;
import at.ac.tuwien.ase.groupphase.backend.mapper.SubmissionMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private final SubmissionService submissionService;
    private final SubmissionMapper submissionMapper;
    private final Logger logger = LoggerFactory.getLogger(LeagueService.class);

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
        this.challengeGenerationService.generateForExpiredChallenges();
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

    public List<LeaderboardDto> getLeaderboardOfLeague(Long leagueId, String currentUsername) {
        logger.trace("Constructing leaderboard with getLeaderboardOfLeague({}, {})", leagueId, currentUsername);
        List<LeaderboardDto> leaderboard = new ArrayList<>();
        for (Participant p : participantRepository.getParticipantRankingForLeague(leagueId)) {
            logger.debug("Got Participant for league with id {}: {Username: {}, Wins: {}}", leagueId, p.getUsername(),
                    p.getWins());
            LeaderboardDto leaderboardDto = new LeaderboardDto(p.getUsername(), p.getWins());
            leaderboard.add(leaderboardDto);
        }

        if (leaderboard.size() < 1) {
            return List.of();
        }

        // Sort the leaderboard based on points in descending order
        Collections.sort(leaderboard);

        // If the first placed participant has 0 wins no ranking possible -> return empty list
        if (leaderboard.get(0).getWins().equals(0)) {
            return List.of();
        }

        int ranking = 1;
        boolean currentUserRanked = false;
        leaderboard.get(0).setPosition(ranking);

        if (leaderboard.get(0).getUsername().equals(currentUsername)) {
            currentUserRanked = true;
        }

        int clearStartIndex = 0;
        int sameRankings = 0;
        // Update the position field based on the sorted order (only show until rank 10 or first 10 participants
        // if a lot of participants have the same rank)
        for (int i = 1; i < leaderboard.size() && ranking < 11 && (10 - (sameRankings - 1) - ranking) >= 0; i++) {
            if (leaderboard.get(i).getUsername().equals(currentUsername)) {
                currentUserRanked = true;
            }
            // If two participants have the same amount of wins, their position should be the same
            // if not, the ranking is increased (= "lower ranking")
            if (!leaderboard.get(i - 1).getWins().equals(leaderboard.get(i).getWins())) {
                ranking++;
                sameRankings = 0;
            } else {
                sameRankings++;
            }

            leaderboard.get(i).setPosition(ranking);

            clearStartIndex = i;
        }

        LeaderboardDto currentUser = null;
        if (!currentUserRanked) {
            for (int i = clearStartIndex; i < leaderboard.size(); i++) {
                if (!leaderboard.get(i - 1).getWins().equals(leaderboard.get(i).getWins())) {
                    ranking++;
                }

                if (leaderboard.get(i).getUsername().equals(currentUsername)) {
                    currentUser = new LeaderboardDto(leaderboard.get(i).getUsername(), leaderboard.get(i).getWins());
                    currentUser.setPosition(ranking);
                    break;
                }
            }
        }

        if (!currentUserRanked) {
            leaderboard.subList(clearStartIndex, leaderboard.size()).clear();
            leaderboard.add(currentUser);
        } else {
            leaderboard.subList(clearStartIndex + 1, leaderboard.size()).clear();
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

    public List<WinningSubmissionDto> getLastWinningSubmissions(Long leagueId) {
        try {
            final Challenge lastChallenge = this.leagueRepository.findLastEndedChallenge(leagueId, LocalDate.now())
                    .orElseThrow();

            final int winningVotes = lastChallenge.getSubmissions().stream()
                    .map(s -> (int) s.getUpVotes().stream().filter(ParticipantSubmissionVote::isUpvote).count())
                    .max(Comparator.naturalOrder()).orElseThrow();

            return lastChallenge.getSubmissions().stream()
                    .filter(s -> winningVotes == (int) s.getUpVotes().stream()
                            .filter(ParticipantSubmissionVote::isUpvote).count())
                    .map(this.submissionService::buildWinningSubmissionDto).collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            throw new NoLatestChallengeException();
        }
    }
}
