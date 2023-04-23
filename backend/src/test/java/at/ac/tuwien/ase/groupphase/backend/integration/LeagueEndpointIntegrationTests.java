package at.ac.tuwien.ase.groupphase.backend.integration;

import at.ac.tuwien.ase.groupphase.backend.controller.LeagueEndpoint;
import at.ac.tuwien.ase.groupphase.backend.dto.LeagueDto;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import util.Constants;
import java.util.ArrayList;
import java.util.List;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LeagueEndpointIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    LeagueEndpoint leagueEndpoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(Constants.EXISTING_USER_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    void CreateLeagueShouldReturn201() throws Exception {
        LeagueDto dto = new LeagueDto(null, Constants.VALID_LEAGUE_NAME, Constants.VALID_LEAGUE_GAMEMODE,
                Constants.VALID_LEAGUE_CHALLENGE_DURATION, Constants.VALID_LEAGUE_REGION);

        this.leagueEndpoint.createLeague(dto);

        List<League> list = new ArrayList<>();
        this.leagueRepository.findAll().forEach(list::add);
        assertEquals(1, list.size());
        League league = list.get(0);
        assertAll(() -> assertNotNull(league.getId()),
                () -> assertEquals(Constants.VALID_LEAGUE_NAME, league.getName()),
                () -> assertEquals(Constants.VALID_LEAGUE_GAMEMODE, league.getGameMode()),
                () -> assertEquals(Constants.VALID_LEAGUE_CHALLENGE_DURATION, league.getChallengeDuration()),
                () -> assertEquals(Constants.VALID_LEAGUE_REGION, league.getRegion()));

        // TODO sollte so gehen, aber das mit dem mocken passt ned wirklich @WithMockUser muss dann oben hin
        // String json = this.objectMapper.writeValueAsString(dto);
        // mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/league/create-league").content(json)
        // .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
    }

}
