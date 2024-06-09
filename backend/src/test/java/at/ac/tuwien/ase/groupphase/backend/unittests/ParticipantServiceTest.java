package at.ac.tuwien.ase.groupphase.backend.unittests;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.service.ParticipantService;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.Constants.*;

@SpringBootTest
@Transactional
public class ParticipantServiceTest {

    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private Participant participant;

    @Autowired
    public ParticipantServiceTest(ParticipantService participantService, ParticipantRepository participantRepository) {
        this.participantService = participantService;
        this.participantRepository = participantRepository;
    }

    @BeforeEach
    void beforeEach() {
        this.participantRepository.deleteAll();
        this.participant = this.participantRepository.save(VALID_PARTICIPANT_1);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(VALID_USER_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void test_getParticipantDto() {
        ParticipantDto actualParticipantDto = this.participantService.getParticipantDto();
        assertEquals(VALID_PARTICIPANT_DTO_1, actualParticipantDto);
    }

    @Test
    void givenParticipant_increaseWinsIncreasesWinsByOne() {
        Participant participantBefore = participantRepository.findById(this.participant.getId()).orElseThrow();
        assertEquals(VALID_PARTICIPANT_1.getWins(), participantBefore.getWins());

        participantService.increaseWinsOfParticipant(this.participant.getId(), 1L);
        Participant participantAfter = participantRepository.findById(this.participant.getId()).orElseThrow();
        assertEquals(1, participantAfter.getWins().get(1L));
    }

}
