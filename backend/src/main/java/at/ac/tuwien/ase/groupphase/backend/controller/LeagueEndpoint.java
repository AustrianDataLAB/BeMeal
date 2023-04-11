package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.LeagueDto;
import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser;
import at.ac.tuwien.ase.groupphase.backend.mapper.LeagueMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.ase.groupphase.backend.service.LeagueService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/v1/league")
public class LeagueEndpoint {

    private final Logger logger = LoggerFactory.getLogger(LeagueEndpoint.class);
    private final LeagueMapper leagueMapper;
    private final LeagueService leagueService;

    @Autowired
    @NotNull
    public LeagueEndpoint(LeagueMapper leagueMapper, final LeagueService leagueService) {
        this.leagueMapper = leagueMapper;
        this.leagueService = leagueService;
    }

    @PostMapping("/create-league")
    @SecurityRequirement(name = "bearerToken")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLeague(@NotNull @RequestBody final LeagueDto leagueDto) {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        League league = this.leagueMapper.leagueDtoToLeague(leagueDto);
        this.leagueService.createLeague(user, league);
    }

    @GetMapping("/leagues")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public List<LeagueDto> getLeagues() {
        String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<League> leagues = this.leagueService.getLeagues(user);
        return this.leagueMapper.leagueListToLeagueDtoList(leagues);
    }
}
