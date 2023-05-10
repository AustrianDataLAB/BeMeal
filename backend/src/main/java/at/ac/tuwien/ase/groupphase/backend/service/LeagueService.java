package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.ChallengeInfoDto;
import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.exception.MissingPictureException;
import at.ac.tuwien.ase.groupphase.backend.exception.NoChallengeException;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class LeagueService {

    private static final String IMAGE_FORMAT = "jpg";
    private static final String IMAGE_PATH = "src/main/resources/recipes";

    private final UserRepository userRepository;
    private final LeagueRepository leagueRepository;
    private final ChallengeRepository challengeRepository;

    private final RecipeService recipeService;
    private final Logger logger = LoggerFactory.getLogger(LeagueService.class);

    @Autowired
    @NotNull
    public LeagueService(UserRepository userRepository, LeagueRepository leagueRepository,
            ChallengeRepository challengeRepository, RecipeService recipeService) {
        this.userRepository = userRepository;
        this.leagueRepository = leagueRepository;
        this.challengeRepository = challengeRepository;
        this.recipeService = recipeService;
    }

    @Transactional
    public ChallengeInfoDto getChallengeForLeague(Long id) {
        League league = this.leagueRepository.findById(id).orElseThrow();
        if (league.getChallenges().isEmpty()) {
            throw new NoChallengeException();
        }

        // TODO dont take first one, but the correct one
        // Challenge challenge = league.getChallenges().get(0);
        Challenge challenge = this.challengeRepository.getLatestChallenge(league.getId());
        RecipeDto recipe = this.recipeService.getRecipeById(challenge.getRecipe());

        ChallengeInfoDto dto = new ChallengeInfoDto();
        dto.setDescription(recipe.getDescription());
        dto.setName(recipe.getName());
        dto.setEndDate(challenge.getEndDate());
        dto.setChallengeId(challenge.getId());

        GameMode gameMode = league.getGameMode();

        if (gameMode == GameMode.PICTURE || gameMode == GameMode.PICTURE_INGREDIENTS) {
            String uuid = recipe.getPictureUUID();
            try {
                Path path = getPath(uuid);
                if (!Files.exists(path))
                    throw new MissingPictureException();
                byte[] bytes = Files.readAllBytes(path);
                String imageString = Base64.getEncoder().withoutPadding().encodeToString(bytes);
                dto.setPicture(imageString);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (gameMode != GameMode.PICTURE) {
            dto.setIngredients(recipe.getIngredients());
        }

        return dto;
    }

    private static Path getPath(String uuid) {
        return Paths.get(IMAGE_PATH, uuid + "." + IMAGE_FORMAT);
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
        };
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
