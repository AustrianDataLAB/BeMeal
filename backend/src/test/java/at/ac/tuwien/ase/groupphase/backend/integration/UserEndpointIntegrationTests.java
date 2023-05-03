package at.ac.tuwien.ase.groupphase.backend.integration;

import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import util.Constants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserEndpointIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParticipantRepository participantRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerPlatformUserShouldReturn201() throws Exception {
        Registration reg = new Registration(Constants.VALID_USER_EMAIL, Constants.VALID_USER_USERNAME,
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
        Registration reg = new Registration(Constants.EXISTING_USER_EMAIL, Constants.EXISTING_USER_USERNAME,
                Constants.EXISTING_USER_PASSWORD, Constants.EXISTING_USER_REGION, Constants.EXISTING_USER_POSTAL_CODE);

        String json = this.objectMapper.writeValueAsString(reg);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/self-service/registration/participant").content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());

    }

    // email valideren noch im backend?
    // @Test
    // void registerPlatformUserWithInalidEmailShouldReturn201() throws Exception {
    // Registration reg = new Registration(Constants.INVALID_EMAIL, Constants.VALID_USER_USERNAME,
    // Constants.VALID_USER_PASSWORD, Constants.VALID_USER_REGION, Constants.VALID_USER_POSTAL_CODE);
    //
    // String json = this.objectMapper.writeValueAsString(reg);
    // this.webTestClient.post().uri("/api/v1/self-service/registration/participant")
    // .contentType(MediaType.APPLICATION_JSON).bodyValue(json).exchange().expectStatus().is4xxClientError();
    // }

    // TODO view profile test

}
