package at.ac.tuwien.ase.groupphase.backend.unittests;

import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.Submission;
import at.ac.tuwien.ase.groupphase.backend.exception.ForbiddenAccessException;
import at.ac.tuwien.ase.groupphase.backend.mapper.SubmissionMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.*;
import at.ac.tuwien.ase.groupphase.backend.service.SubmissionService;
import jakarta.transaction.Transactional;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static util.Constants.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SubmissionServiceTest {
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private SubmissionMapper submissionMapper;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private VoteRepository voteRepository;

    private Challenge ch;
    private Participant p1;
    private Participant p2;
    private Participant p3;
    private Submission s1;
    private Submission s2;

    @BeforeEach
    void beforeEach() {
        p1 = participantRepository.save(VALID_PARTICIPANT_1);
        p2 = participantRepository.save(VALID_PARTICIPANT_2);
        p3 = participantRepository.save(VALID_PARTICIPANT_3);

        League l = LEAGUE1;
        List<Participant> participants = new ArrayList<>();
        participants.add(p1);
        participants.add(p2);
        l.setParticipants(participants);
        l = leagueRepository.save(l);

        List<League> leagues = new ArrayList<>();
        leagues.add(l);
        p1.setLeagues(leagues);
        p2.setLeagues(leagues);

        CHALLENGE1.setLeague(l);

        ch = challengeRepository.save(CHALLENGE1);
        // l.setChallenges(List.of(ch));

        s1 = VALID_SUBMISSION_P1;
        s2 = VALID_SUBMISSION_P2;
        s1.setChallenge(ch);
        s1.setParticipant(p1);
        s1.setPicture(VALID_PICTURE_UUID1);
        s2.setPicture(VALID_PICTURE_UUID2);
        s2.setChallenge(ch);
        s2.setParticipant(p2);

        s1 = submissionRepository.save(s1);
        s2 = submissionRepository.save(s2);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(VALID_USER_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

    }

    @AfterEach
    void afterEach() {
        voteRepository.deleteAll();
        submissionRepository.deleteAll();
        challengeRepository.deleteAll();
        leagueRepository.deleteAll();
        participantRepository.deleteAll();
    }

    @Test
    public void getSubmissionWithValidId() {
        SubmissionDto dto = this.submissionService.getSubmission(this.s1.getId().toString());
        assertNotNull(dto);
        assertNotNull(dto.getDate());
        assertNotNull(dto.getId());
        assertNotNull(dto.getPicture());
    }

    @Test
    public void getSubmissionWithUserNotInLeagueShouldThrowForbiddenAccessException() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(VALID_USER_USERNAME3);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(ForbiddenAccessException.class,
                () -> this.submissionService.getSubmission(this.s1.getId().toString()));

    }

    @Test
    public void getCurrentSubmissionWithValidInput() {
        SubmissionDto dto = this.submissionService.getCurrentSubmission(this.ch.getId().toString());
        assertNotNull(dto);
        assertNotNull(dto.getDate());
        assertNotNull(dto.getId());
        assertNotNull(dto.getPicture());
    }

    @Test
    public void getCurrentSubmissionWithNoSubmissions() {
        submissionRepository.deleteAll();
        SubmissionDto dto = this.submissionService.getCurrentSubmission(this.ch.getId().toString());
        assertNull(dto);
    }

    @Test
    public void voidgetCurrentSubmissionWithParticipantThatIsNotInLeagueShouldThrowForbiddenAccessException() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(VALID_USER_USERNAME3);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Submission s = VALID_SUBMISSION_P1;
        s.setChallenge(ch);
        s.setParticipant(p3);
        s.setPicture(VALID_PICTURE_UUID1);
        submissionRepository.save(s);

        assertThrows(ForbiddenAccessException.class,
                () -> this.submissionService.getCurrentSubmission(this.ch.getId().toString()));
    }

    // @Test
    // public void noSubmissionsUpvoted_userShouldGetOneSubmissionToUpvote() {
    // List<SubmissionDto> submissions = submissionService.getNotVotedSubmissionsOfUser(ch.getId(), p1.getUsername());
    // assertEquals(1, submissions.size());
    //
    // SubmissionDto s1Dto = submissionMapper
    // .submissionToSubmissionDto(submissionRepository.findById(s1.getId()).orElseThrow());
    // SubmissionDto s2Dto = submissionMapper
    // .submissionToSubmissionDto(submissionRepository.findById(s2.getId()).orElseThrow());
    //
    // assertEquals(s2Dto, submissions.get(0));
    // assertNotEquals(s1Dto, submissions.get(0));
    // }

    @Test
    public void afterSubmissionUpvoted_userShouldGetNoSubmissionToUpvote() {
        submissionService.saveVote(s2.getId(), p1.getUsername(), true);

        List<SubmissionDto> submissions = submissionService.getNotVotedSubmissionsOfUser(ch.getId(), p1.getUsername());
        assertEquals(0, submissions.size());
    }

    @Test
    public void submitWithValidInputShouldWork() throws FileNotFoundException {
        MultipartFile file = getMultiPartFile(VALID_PICTURE_UUID1);
        assertNotNull(file);
        submissionRepository.deleteAll();
        assertEquals(false, this.submissionRepository.findAll().iterator().hasNext());
        this.submissionService.submit(file, this.ch.getId().toString());
        assertEquals(true, this.submissionRepository.findAll().iterator().hasNext());

    }

//    @Test
//    public void submitWhenChallengeHasNotYetStartet() throws FileNotFoundException {
//        MultipartFile file = getMultiPartFile(VALID_PICTURE_UUID1);
//        assertNotNull(file);
//
//        Mockito.when(LocalDate.now()).thenReturn(LocalDate.of(1900,1,1));
//
//        assertThrows(ForbiddenAccessException.class,
//                () -> this.submissionService.submit(file, this.ch.getId().toString()));
//    }

    private MultipartFile getMultiPartFile(UUID uuid) throws FileNotFoundException {
        Path path = Paths.get("src/main/resources/static/img/" + uuid + ".png");

        String name = uuid + ".png";
        String originalFileName = uuid + ".png";
        String contentType = "image/png";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);
        return result;

    }

}
