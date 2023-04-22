package at.ac.tuwien.ase.groupphase.backend;

import at.ac.tuwien.ase.groupphase.backend.controller.SelfService;
import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.entity.Region;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class SelfServiceIntegrationTests {

    @Autowired
    private WebTestClient webTestClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerPlatformUserShouldReturn201() throws Exception {
        Registration reg = new Registration("test1@gmail.com", "fran1z", "12345678", Region.STYRIA, "2340");

        String json = this.objectMapper.writeValueAsString(reg);
        this.webTestClient.post().uri("/api/v1/self-service/registration/participant")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(json).exchange().expectStatus().isCreated();
        // mockMvc.perform(post("/api/v1/self-service/registration/participant").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isCreated());
    }

    @Test
    void registerPlatformUserShouldReturn2011() throws Exception {
        Registration reg = new Registration("test1@gmail.com", "fran1z", "12345678", Region.STYRIA, "2340");

        String json = this.objectMapper.writeValueAsString(reg);
        this.webTestClient.post().uri("/api/v1/self-service/registration/participant")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(json).exchange().expectStatus().isCreated();
        // mockMvc.perform(post("/api/v1/self-service/registration/participant").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isCreated());
    }

}
