package at.ac.tuwien.ase.groupphase.backend.integrationtest;

import at.ac.tuwien.ase.groupphase.backend.dto.LeaderboardDto;
import at.ac.tuwien.ase.groupphase.backend.endpoint.LeagueEndpoint;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import util.Constants;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotEmpty;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void CreateLeagueShouldReturn201() throws Exception {
        // todo fix test, failed because for challenge creation docker is necessary
        /*
         * LeagueDto dto = new LeagueDto(null, Constants.VALID_LEAGUE_NAME, Constants.VALID_LEAGUE_GAMEMODE,
         * Constants.VALID_LEAGUE_CHALLENGE_DURATION, Constants.VALID_LEAGUE_REGION, List.of());
         *
         * this.leagueEndpoint.createLeague(dto);
         *
         * List<League> list = new ArrayList<>(); this.leagueRepository.findAll().forEach(list::add); assertEquals(1,
         * list.size()); League league = list.get(0); assertAll(() -> assertNotNull(league.getId()), () ->
         * assertEquals(Constants.VALID_LEAGUE_NAME, league.getName()), () ->
         * assertEquals(Constants.VALID_LEAGUE_GAMEMODE, league.getGameMode()), () ->
         * assertEquals(Constants.VALID_LEAGUE_CHALLENGE_DURATION, league.getChallengeDuration()), () ->
         * assertEquals(Constants.VALID_LEAGUE_REGION, league.getRegion()));
         */
        // String json = this.objectMapper.writeValueAsString(dto);
        // mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/league/create-league").content(json)
        // .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
    }

    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void givenPlatformUsersInLeague_getLeaderboardIsNotEmpty() throws Exception {
        MvcResult response = mockMvc
                .perform(MockMvcRequestBuilders.get(Constants.LEAGUE_ENDPOINT_BASEURI + "/1/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andReturn();

        List<LeaderboardDto> leaderboard = objectMapper.readValue(response.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertNotNull(leaderboard);
        assertNotEmpty(leaderboard, "Leaderboard list should not be empty");
        assertEquals(3, leaderboard.size());
    }

    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void givenPlatformUsersInLeague_LeaderboardRankingsAreCorrect() throws Exception {
        MvcResult response = mockMvc
                .perform(MockMvcRequestBuilders.get(Constants.LEAGUE_ENDPOINT_BASEURI + "/1/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andReturn();

        List<LeaderboardDto> leaderboard = objectMapper.readValue(response.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(3, leaderboard.size());

        assertEquals(1, leaderboard.get(0).getPosition());
        assertEquals(1, leaderboard.get(1).getPosition());
        assertEquals(2, leaderboard.get(2).getPosition());
    }

    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void givenPlatformUsersInLeague_getLeaderboardisCappedAt10PlusCurrentUser() throws Exception {
        MvcResult response = mockMvc
                .perform(MockMvcRequestBuilders.get(Constants.LEAGUE_ENDPOINT_BASEURI + "/2/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andReturn();

        List<LeaderboardDto> leaderboard = objectMapper.readValue(response.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertNotNull(leaderboard);
        assertNotEmpty(leaderboard, "Leaderboard list should not be empty");
        assertEquals(10 + 1, leaderboard.size());

        for (int i = 0; i < leaderboard.size() - 1; i++) {
            assertEquals(1, leaderboard.get(i).getPosition());
        }
        assertEquals(2, leaderboard.get(leaderboard.size() - 1).getPosition());
    }

    /*
     * @Test
     *
     * @Sql({ "classpath:sql/SelfServiceData.sql" })
     *
     * @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD) void
     * givenPlatformUsersInLeague_getLeaderboardOfInvalidLeageShouldReturn404() throws Exception {
     * mockMvc.perform(MockMvcRequestBuilders.get(Constants.LEAGUE_ENDPOINT_BASEURI + "/1000/leaderboard")
     * .contentType(MediaType.APPLICATION_JSON)) .andDo(MockMvcResultHandlers.print())
     * .andExpect(status().isNotFound()); }
     */

}
