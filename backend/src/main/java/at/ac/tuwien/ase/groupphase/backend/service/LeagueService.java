package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.controller.InvitationService;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
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
    private final Logger logger = LoggerFactory.getLogger(InvitationService.class);

    @Autowired
    @NotNull
    public LeagueService(UserRepository userRepository, LeagueRepository leagueRepository) {
        this.userRepository = userRepository;
        this.leagueRepository = leagueRepository;
    }

    public void createLeague(String username, League league) {
        Participant user = (Participant) this.userRepository.findByUsername(username);
        List<Participant> participantList = new ArrayList<>();
        participantList.add(user);
        league.setParticipants(participantList);
        this.leagueRepository.save(league);
        // TODO add owner relation
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
