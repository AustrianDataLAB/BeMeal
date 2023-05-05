package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.ChallengeInfoDto;
import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class LeagueService {

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
        Challenge challenge = league.getChallenges().get(0);
        RecipeDto recipe = this.recipeService.getRecipeById(challenge.getRecipe());

        ChallengeInfoDto dto = new ChallengeInfoDto();
        dto.setDescription(recipe.getDescription());
        dto.setIngredients(recipe.getIngredients());
        dto.setName(recipe.getName());
        dto.setEndDate(challenge.getEndDate());
        dto.setChallengeId(challenge.getId());
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

    public List<League> getLeagues(String username) {
        Participant user = (Participant) this.userRepository.findByUsername(username);
        List<Participant> participantList = new ArrayList<>();
        participantList.add(user);
        return this.leagueRepository.findLeaguesByParticipantsIn(new HashSet<>(participantList));
    }
}
