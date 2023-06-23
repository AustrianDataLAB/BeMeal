package at.ac.tuwien.ase.groupphase.backend.integrationtest;

import at.ac.tuwien.ase.groupphase.backend.dto.LeaderboardDto;
import at.ac.tuwien.ase.groupphase.backend.dto.LeagueSecretsDto;
import at.ac.tuwien.ase.groupphase.backend.endpoint.InvitationEndpoint;
import at.ac.tuwien.ase.groupphase.backend.endpoint.LeagueEndpoint;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.service.LeagueService;
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotEmpty;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
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

    @Autowired
    InvitationEndpoint invitationEndpoint;

    @Autowired
    ParticipantRepository participantRepository;

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

    // TODO fix test after wins was changed to map
    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void givenPlatformUsersInLeague_getLeaderboardIsNotEmpty() throws Exception {
        Participant p1 = this.participantRepository.findByUsername("John");
        Participant p2 = this.participantRepository.findByUsername("John2");
        Participant p3 = this.participantRepository.findByUsername("John3");
        Map<Long, Integer> map1 = new HashMap<>();
        map1.put(1L, 5);
        p1.setWins(map1);
        Map<Long, Integer> map2 = new HashMap<>();
        map2.put(1L, 6);
        p2.setWins(map2);
        Map<Long, Integer> map3 = new HashMap<>();
        map3.put(1L, 7);
        p3.setWins(map3);

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

    // TODO fix test after wins was changed to map
    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void givenPlatformUsersInLeague_LeaderboardRankingsAreCorrect() throws Exception {
        Participant user = (Participant) this.participantRepository.findByUsername(Constants.EXISTING_USER_USERNAME);
        Map<Long, Integer> map = new HashMap<>();
        map.put(1L, 0);
        user.setWins(map);
        Participant p1 = this.participantRepository.findByUsername("John");
        Participant p2 = this.participantRepository.findByUsername("John2");
        Participant p3 = this.participantRepository.findByUsername("John3");
        Map<Long, Integer> map1 = new HashMap<>();
        map1.put(1L, 5);
        p1.setWins(map1);
        Map<Long, Integer> map2 = new HashMap<>();
        map2.put(1L, 7);
        p2.setWins(map2);
        Map<Long, Integer> map3 = new HashMap<>();
        map3.put(1L, 7);
        p3.setWins(map3);
        this.participantRepository.save(user);

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

    // TODO fix test after wins was changed to map
    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void givenPlatformUsersInLeague_getLeaderboardisCappedAt10PlusCurrentUser() throws Exception {
        Participant p1 = this.participantRepository.findByUsername("Maria");
        Participant p2 = this.participantRepository.findByUsername("Maria2");
        Participant p3 = this.participantRepository.findByUsername("Maria3");
        Participant p4 = this.participantRepository.findByUsername("Jose");
        Participant p5 = this.participantRepository.findByUsername("Jose2");
        Participant p6 = this.participantRepository.findByUsername("Jose3");
        Participant p7 = this.participantRepository.findByUsername("Arnold");
        Participant p8 = this.participantRepository.findByUsername("TheLegend27");
        Participant p9 = this.participantRepository.findByUsername("John");
        Participant p10 = this.participantRepository.findByUsername("John2");
        Participant p11 = this.participantRepository.findByUsername("John3");
        Map<Long, Integer> map1 = new HashMap<>();
        map1.put(2L, 7);
        p1.setWins(map1);

        Map<Long, Integer> map2 = new HashMap<>();
        map2.put(2L, 6);
        p2.setWins(map2);

        p3.setWins(map1);
        p4.setWins(map1);
        p5.setWins(map1);
        p6.setWins(map1);
        p7.setWins(map1);
        p8.setWins(map1);
        p9.setWins(map1);
        p10.setWins(map1);
        p11.setWins(map1);

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

    @Test
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    void getHiddenIdentifierWithInvalidLeagueIdShouldThrow403NotCreaterOfLeague() throws Exception {
        MvcResult response = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/invitation/hidden-identifier/-1/false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    void getHiddenIdentifierWithCorrectIdAndNoRefreshShouldReturnOldHiddenIdentifier() throws Exception {
        Participant p = this.participantRepository.findByUsername(Constants.EXISTING_USER_USERNAME);
        League l = this.leagueRepository.findLeagueByName("League1");
        List<League> ownerOf = new ArrayList<>();
        ownerOf.add(l);
        p.setOwnerOf(ownerOf);
        this.participantRepository.save(p);

        MvcResult response = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/invitation/hidden-identifier/1/false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andReturn();

        LeagueSecretsDto ret = objectMapper.readValue(response.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals("ef2133ad-f5e9-4aac-bc1f-46dcf62f495e", ret.hiddenIdentifier().toString());
    }

    @Test
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    void getHiddenIdentifierWithCorrectIdAndRefreshShouldReturnOldHiddenIdentifier() throws Exception {
        Participant p = this.participantRepository.findByUsername(Constants.EXISTING_USER_USERNAME);
        League l = this.leagueRepository.findLeagueByName("League1");
        List<League> ownerOf = new ArrayList<>();
        ownerOf.add(l);
        p.setOwnerOf(ownerOf);
        this.participantRepository.save(p);

        MvcResult response = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/invitation/hidden-identifier/1/true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andReturn();

        LeagueSecretsDto ret = objectMapper.readValue(response.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertNotEquals("ef2133ad-f5e9-4aac-bc1f-46dcf62f495e", ret.hiddenIdentifier().toString());
    }

    // @Test
    // @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    // @Sql({ "classpath:sql/SelfServiceData.sql" })
    // void getHiddenIdentifierWithInvalidLeagueIdShouldThrow404() throws Exception {
    // LeagueService leagueService = Mockito.mock(LeagueService.class);
    // Mockito.when(leagueService.isUserCreatorOfLeague("John", -1)).thenReturn(true);
    //
    //
    // invitationEndpoint.getHiddenIdentifier(-1, true, new Principal() {
    // @Override
    // public String getName() {
    // return "John";
    // }
    // });
    // MvcResult response = mockMvc
    // .perform(MockMvcRequestBuilders.get("/api/v1/invitation/hidden-identifier/-1/false")
    // .contentType(MediaType.APPLICATION_JSON))
    // .andDo(MockMvcResultHandlers.print()).andExpect(status().isNotFound()).andReturn();
    // }

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
