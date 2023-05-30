package at.ac.tuwien.ase.groupphase.backend.unittests;

import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.Submission;
import at.ac.tuwien.ase.groupphase.backend.exception.ForbiddenAccessException;
import at.ac.tuwien.ase.groupphase.backend.entity.*;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
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
    private Submission s1;
    private Submission s2;

    @BeforeEach
    void beforeEach() {
        p1 = participantRepository.save(VALID_PARTICIPANT_1);
        p2 = participantRepository.save(VALID_PARTICIPANT_2);

        League l = leagueRepository.save(LEAGUE1);
        CHALLENGE1.setLeague(l);

        ch = challengeRepository.save(CHALLENGE1);
        // l.setChallenges(List.of(ch));

        s1 = VALID_SUBMISSION_P1;
        s2 = VALID_SUBMISSION_P2;
        s1.setChallenge(ch);
        s1.setParticipant(p1);
        s2.setChallenge(ch);
        s2.setParticipant(p2);

        s1 = submissionRepository.save(s1);
        s2 = submissionRepository.save(s2);

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
    public void noSubmissionsUpvoted_userShouldGetOneSubmissionToUpvote() {
        List<SubmissionDto> submissions = submissionService.getNotVotedSubmissionsOfUser(ch.getId(), p1.getUsername());
        assertEquals(1, submissions.size());

        SubmissionDto s1Dto = submissionMapper
                .submissionToSubmissionDto(submissionRepository.findById(s1.getId()).orElseThrow());
        SubmissionDto s2Dto = submissionMapper
                .submissionToSubmissionDto(submissionRepository.findById(s2.getId()).orElseThrow());

        assertEquals(s2Dto, submissions.get(0));
        assertNotEquals(s1Dto, submissions.get(0));
    }

    @Test
    public void afterSubmissionUpvoted_userShouldGetNoSubmissionToUpvote() {
        submissionService.saveVote(s2.getId(), p1.getUsername(), true);

        List<SubmissionDto> submissions = submissionService.getNotVotedSubmissionsOfUser(ch.getId(), p1.getUsername());
        assertEquals(0, submissions.size());
    }

    @Test
    public void givenSubmissionsWithUpvotes_GetWinningSubmissionNotEmpty() {
        submissionService.saveVote(s2.getId(), p1.getUsername(), true);

        SubmissionWithUpvotes sub = submissionService.getWinningSubmissionForChallange(ch.getId());
        assertNotNull(sub, "Submission must not be null");
        assertEquals(s2.getId(), sub.getSubmission().getId());
    }
}
