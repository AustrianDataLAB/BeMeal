package at.ac.tuwien.ase.groupphase.backend.integrationtest;

import at.ac.tuwien.ase.groupphase.backend.dto.PasswordResetDto;
import at.ac.tuwien.ase.groupphase.backend.dto.RegistrationDto;
import at.ac.tuwien.ase.groupphase.backend.endpoint.UserEndpoint;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserEndpointIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserEndpoint userEndpoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Sql({ "classpath:sql/RegionalLeague.sql" })
    void registerPlatformUserShouldReturn201() throws Exception {
        RegistrationDto reg = new RegistrationDto(Constants.VALID_USER_EMAIL, Constants.VALID_USER_USERNAME,
                Constants.VALID_USER_PASSWORD, Constants.VALID_USER_REGION, Constants.VALID_USER_POSTAL_CODE);
        String json = this.objectMapper.writeValueAsString(reg);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/self-service/registration/participant").content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        List<Participant> list1 = new ArrayList<>();
        this.participantRepository.findAll().forEach(list1::add);
        assertEquals(1, list1.size());
    }

    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    void registerPlatformUserTwiceShouldReturn409() throws Exception {
        RegistrationDto reg = new RegistrationDto(Constants.EXISTING_USER_EMAIL, Constants.EXISTING_USER_USERNAME,
                Constants.EXISTING_USER_PASSWORD, Constants.EXISTING_USER_REGION, Constants.EXISTING_USER_POSTAL_CODE);

        String json = this.objectMapper.writeValueAsString(reg);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/self-service/registration/participant").content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());

    }

    @Test
    void registerPlatformUserWithInvalidPostalCodeShouldReturn422() throws Exception {
        RegistrationDto reg = new RegistrationDto(Constants.EXISTING_USER_EMAIL, Constants.EXISTING_USER_USERNAME,
                Constants.EXISTING_USER_PASSWORD, Constants.EXISTING_USER_REGION,
                Constants.NON_EXISTING_USER_POSTAL_CODE);

        String json = this.objectMapper.writeValueAsString(reg);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/self-service/registration/participant").content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity());

    }

    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void resetPasswordShouldWork() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(Constants.EXISTING_USER_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UUID pwResetToken = UUID.randomUUID();
        Participant p = this.participantRepository.findByUsername(Constants.EXISTING_USER_USERNAME);
        p.setPasswordResetToken(pwResetToken);

        byte[] oldPw = p.getPassword();

        PasswordResetDto dto = new PasswordResetDto("11111111111");

        String json = this.objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/self-service/password/" + pwResetToken.toString())
                .content(json).contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent()).andReturn();

        p = this.participantRepository.findByUsername(Constants.EXISTING_USER_USERNAME);
        assertNull(p.getPasswordResetToken());
        assertNotEquals(oldPw, p.getPassword());
    }

    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void resetPasswordWithInvalidToken() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(Constants.EXISTING_USER_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Participant p = this.participantRepository.findByUsername(Constants.EXISTING_USER_USERNAME);

        byte[] oldPw = p.getPassword();

        PasswordResetDto dto = new PasswordResetDto("11111111111");

        String json = this.objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/self-service/password/" + UUID.randomUUID()).content(json)
                .contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent()).andReturn();

        p = this.participantRepository.findByUsername(Constants.EXISTING_USER_USERNAME);
        assertNull(p.getPasswordResetToken());
        assertEquals(oldPw, p.getPassword());
    }

    @Test
    @Sql({ "classpath:sql/SelfServiceData.sql" })
    @WithMockUser(username = Constants.EXISTING_USER_USERNAME, password = Constants.EXISTING_USER_PASSWORD)
    void requestPasswordResetWithInvalidEmailShouldSetToken() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(Constants.EXISTING_USER_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Participant p = this.participantRepository.findByUsername(Constants.EXISTING_USER_USERNAME);

        assertNull(p.getPasswordResetToken());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/self-service/password-token/John@gmgail.com")
                .contentType(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound()).andReturn();
    }

}
