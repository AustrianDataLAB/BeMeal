package at.ac.tuwien.ase.groupphase.backend.integration;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.service.ParticipantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static util.Constants.*;

@SpringBootTest
public class ParticipantServiceTest {

    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;

    @Autowired
    public ParticipantServiceTest(ParticipantService participantService, ParticipantRepository participantRepository) {
        this.participantService = participantService;
        this.participantRepository = participantRepository;
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
    void test_getParticipantDto() {
        ParticipantDto actualParticipantDto = this.participantService.getParticipantDto();
        Assertions.assertEquals(VALID_PARTICIPANT_DTO_1, actualParticipantDto);
    }

}
