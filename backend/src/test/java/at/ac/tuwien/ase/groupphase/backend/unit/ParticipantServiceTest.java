package at.ac.tuwien.ase.groupphase.backend.unit;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
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

import java.time.LocalDateTime;

import static util.Constants.*;

@SpringBootTest
public class ParticipantServiceTest {

    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;

    private final Participant participant = new Participant();
    private final ParticipantDto participantDto;

    @Autowired
    public ParticipantServiceTest(ParticipantService participantService, ParticipantRepository participantRepository) {
        this.participantService = participantService;
        this.participantRepository = participantRepository;

        this.participant.setPostalCode(VALID_USER_POSTAL_CODE);
        this.participant.setWins(VALID_WINS);
        this.participant.setRegion(VALID_USER_REGION);
        LocalDateTime localDateTime = LocalDateTime.of(
                1990,
                1,
                1,
                1,
                1
        );
        this.participant.setRegistered(localDateTime);
        this.participant.setId(12121212L);
        this.participant.setEmail(VALID_USER_EMAIL);
        this.participant.setPassword(VALID_USER_PASSWORD_BYTES);
        this.participant.setUsername(VALID_USER_USERNAME);
        this.participant.setIsAdmin(Boolean.FALSE);

        this.participantDto = new ParticipantDto(
                VALID_USER_USERNAME,
                VALID_USER_EMAIL,
                VALID_USER_POSTAL_CODE,
                VALID_WINS,
                VALID_USER_REGION,
                localDateTime
        );
    }

    @BeforeEach
    void beforeEach() {
        this.participantRepository.deleteAll();
        this.participantRepository.save(this.participant);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(VALID_USER_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void test_getParticipantDto() {
        ParticipantDto actualParticipantDto = this.participantService.getParticipantDto();
        Assertions.assertEquals(this.participantDto, actualParticipantDto);
    }

}
