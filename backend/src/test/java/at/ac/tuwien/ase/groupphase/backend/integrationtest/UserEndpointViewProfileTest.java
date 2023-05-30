package at.ac.tuwien.ase.groupphase.backend.integrationtest;

import at.ac.tuwien.ase.groupphase.backend.endpoint.UserEndpoint;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static util.Constants.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserEndpointViewProfileTest {

    // @Autowired
    // private MockMvc mockMvc;

    private final ParticipantRepository participantRepository;
    private final UserEndpoint userEndpoint;

    @Autowired
    public UserEndpointViewProfileTest(ParticipantRepository participantRepository, UserEndpoint userEndpoint) {
        this.participantRepository = participantRepository;
        this.userEndpoint = userEndpoint;
    }

    @BeforeEach
    void beforeEach() {
        this.participantRepository.deleteAll();
        this.participantRepository.save(VALID_PARTICIPANT_1);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(VALID_USER_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void test_viewProfile_method() {
        Assertions.assertEquals(VALID_PARTICIPANT_DTO_1, this.userEndpoint.viewProfile());
    }

    // @Test
    // @WithUserDetails(VALID_USER_USERNAME)
    // void test_viewProfile_request() throws Exception {
    // this.participantRepository.save(VALID_PARTICIPANT_1);
    //// Assertions.assertEquals(VALID_PARTICIPANT_DTO_1, this.selfService.viewProfile());
    // MvcResult mvcResult = mockMvc.perform(
    // MockMvcRequestBuilders.get("/api/v1/self-service/profile")
    // ).andReturn();
    // MockHttpServletResponse response = mvcResult.getResponse();
    //
    // System.out.println(response);
    // System.out.println(response.getContentAsString());
    // }

}
