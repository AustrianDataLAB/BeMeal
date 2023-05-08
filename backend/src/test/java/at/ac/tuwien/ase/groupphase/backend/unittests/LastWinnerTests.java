package at.ac.tuwien.ase.groupphase.backend.unittests;

import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.mapper.LeagueMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.SubmissionRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@SpringBootTest
class LastWinnerTests {

    private final EntityManager entityManager;
    private final ChallengeRepository challengeRepository;
    private final ParticipantRepository participantRepository;
    private final LeagueRepository leagueRepository;
    private final SubmissionRepository submissionRepository;
    private final LeagueMapper leagueMapper;

    private Participant winner = null;
    private Participant p1 = null;
    private Participant p2 = null;
    private Participant p3 = null;
    private Participant p4 = null;
    private League league = null;

    @Autowired
    public LastWinnerTests(final EntityManager entityManager, final ChallengeRepository challengeRepository,
            final ParticipantRepository participantRepository, final LeagueRepository leagueRepository,
            final SubmissionRepository submissionRepository, final LeagueMapper leagueMapper) {
        this.entityManager = entityManager;
        this.challengeRepository = challengeRepository;
        this.participantRepository = participantRepository;
        this.leagueRepository = leagueRepository;
        this.submissionRepository = submissionRepository;
        this.leagueMapper = leagueMapper;
    }

    @BeforeEach
    @Transactional
    public void reset() {
        this.submissionRepository.deleteAll();
        this.flush();
        this.challengeRepository.deleteAll();
        this.flush();
        this.participantRepository.deleteAll();
        this.flush();
        this.leagueRepository.deleteAll();
        this.flush();

        final var p0 = this.generateParticipant("P0");
        final var p1 = this.generateParticipant("P1");
        final var p2 = this.generateParticipant("P2");
        final var p3 = this.generateParticipant("P3");
        final var p4 = this.generateParticipant("P4");
        final var n0 = this.generateParticipant("N0");
        final var n1 = this.generateParticipant("N1");
        this.participantRepository.saveAll(List.of(p0, p1, p2, p3, p4, n0, n1));

        this.flush();

        this.winner = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;

        final var league = new League();
        league.setName("Test League");
        league.setRegion(Region.BURGENLAND);
        league.setChallengeDuration(4);
        league.setGameMode(GameMode.PICTURE_INGREDIENTS);
        league.setParticipants(List.of(p0, p1, p2, p3, p4));

        final var noiseLeague = new League();
        noiseLeague.setName("Noise League");
        noiseLeague.setRegion(Region.BURGENLAND);
        noiseLeague.setChallengeDuration(4);
        noiseLeague.setGameMode(GameMode.PICTURE_INGREDIENTS);
        noiseLeague.setParticipants(List.of(p0, n0, n1));

        this.leagueRepository.saveAll(List.of(league, noiseLeague));
        this.flush();
        this.league = league;

        final var oldChallenge = new Challenge();
        oldChallenge.setDescription("An old challenge");
        oldChallenge.setLeague(league);
        oldChallenge.setStartDate(LocalDate.now().minusDays(100));
        oldChallenge.setEndDate(LocalDate.now().minusDays(97));
        oldChallenge.setRecipe("Nothing");

        final var noiseChallenge = new Challenge();
        noiseChallenge.setDescription("A noise challenge");
        noiseChallenge.setLeague(noiseLeague);
        noiseChallenge.setStartDate(LocalDate.now().minusDays(100));
        noiseChallenge.setEndDate(LocalDate.now().minusDays(97));
        noiseChallenge.setRecipe("Nothing");

        this.challengeRepository.saveAll(List.of(oldChallenge, noiseChallenge));
        this.flush();

        final var winnerSubmission = new Submission();
        winnerSubmission.setPicture(UUID.randomUUID());
        winnerSubmission.setDate(LocalDateTime.now());
        winnerSubmission.setChallenge(oldChallenge);
        winnerSubmission.setParticipant(p0);
        winnerSubmission.setUpVotes(List.of(p1, p2, p3));

        final var anotherSubmission = new Submission();
        anotherSubmission.setPicture(UUID.randomUUID());
        anotherSubmission.setDate(LocalDateTime.now());
        anotherSubmission.setChallenge(oldChallenge);
        anotherSubmission.setParticipant(p1);
        anotherSubmission.setUpVotes(List.of(p0));

        final var noiseSubmission = new Submission();
        noiseSubmission.setPicture(UUID.randomUUID());
        noiseSubmission.setDate(LocalDateTime.now());
        noiseSubmission.setChallenge(noiseChallenge);
        noiseSubmission.setParticipant(p0);
        noiseSubmission.setUpVotes(List.of(n0, n1));

        this.submissionRepository.saveAll(List.of(winnerSubmission, anotherSubmission, noiseSubmission));
        this.flush();
    }

    @Test
    @Transactional
    void noWinnerOnNoSubmissions() {
        final var challenge = new Challenge();
        challenge.setDescription("Current Challenge");
        challenge.setLeague(this.league);
        challenge.setStartDate(LocalDate.now().minusDays(4));
        challenge.setEndDate(LocalDate.now().minusDays(1));
        challenge.setRecipe("Nothing");
        this.challengeRepository.save(challenge);
        this.flush();

        final var leagueDto = this.leagueMapper.leagueToLeagueDto(this.league);
        assertEquals(0, leagueDto.lastWinners().size());
    }

    @Test
    @Transactional
    void noWinnerOnZeroUpVotes() {
        final var challenge = new Challenge();
        challenge.setDescription("Current Challenge");
        challenge.setLeague(this.league);
        challenge.setStartDate(LocalDate.now().minusDays(4));
        challenge.setEndDate(LocalDate.now().minusDays(1));
        challenge.setRecipe("Nothing");
        this.challengeRepository.save(challenge);
        this.flush();

        final var submission = new Submission();
        submission.setDate(LocalDateTime.now());
        submission.setPicture(UUID.randomUUID());
        submission.setChallenge(challenge);
        submission.setParticipant(this.winner);
        submission.setUpVotes(List.of());

        this.submissionRepository.save(submission);
        this.flush();
        final var leagueDto = this.leagueMapper.leagueToLeagueDto(this.league);
        assertEquals(0, leagueDto.lastWinners().size());
    }

    @Test
    @Transactional
    void exactlyOneWinner() {
        final var challenge = new Challenge();
        challenge.setDescription("Current Challenge");
        challenge.setLeague(this.league);
        challenge.setStartDate(LocalDate.now().minusDays(4));
        challenge.setEndDate(LocalDate.now().minusDays(1));
        challenge.setRecipe("Nothing");
        this.challengeRepository.save(challenge);
        this.flush();

        final var loosingSubmission = new Submission();
        loosingSubmission.setDate(LocalDateTime.now());
        loosingSubmission.setPicture(UUID.randomUUID());
        loosingSubmission.setChallenge(challenge);
        loosingSubmission.setParticipant(this.p1);
        loosingSubmission.setUpVotes(List.of(this.winner));

        final var submission = new Submission();
        submission.setDate(LocalDateTime.now());
        submission.setPicture(UUID.randomUUID());
        submission.setChallenge(challenge);
        submission.setParticipant(this.winner);
        submission.setUpVotes(List.of(this.p1, this.p2, this.p4));

        this.submissionRepository.save(submission);
        this.submissionRepository.save(loosingSubmission);
        this.flush();
        final var leagueDto = this.leagueMapper.leagueToLeagueDto(this.league);
        assertEquals(1, leagueDto.lastWinners().size());
        assertIterableEquals(List.of(this.winner.getUsername()), leagueDto.lastWinners());
    }

    @Test
    @Transactional
    void multipleWinnersOnExAequo() {
        final var challenge = new Challenge();
        challenge.setDescription("Current Challenge");
        challenge.setLeague(this.league);
        challenge.setStartDate(LocalDate.now().minusDays(4));
        challenge.setEndDate(LocalDate.now().minusDays(1));
        challenge.setRecipe("Nothing");
        this.challengeRepository.save(challenge);
        this.flush();

        final var submission = new Submission();
        submission.setDate(LocalDateTime.now());
        submission.setPicture(UUID.randomUUID());
        submission.setChallenge(challenge);
        submission.setParticipant(this.p1);
        submission.setUpVotes(List.of(this.winner));

        final var anotherSubmission = new Submission();
        anotherSubmission.setDate(LocalDateTime.now());
        anotherSubmission.setPicture(UUID.randomUUID());
        anotherSubmission.setChallenge(challenge);
        anotherSubmission.setParticipant(this.winner);
        anotherSubmission.setUpVotes(List.of(this.p2));

        this.submissionRepository.save(anotherSubmission);
        this.submissionRepository.save(submission);
        this.flush();
        final var leagueDto = this.leagueMapper.leagueToLeagueDto(this.league);
        assertEquals(2, leagueDto.lastWinners().size());
        assertEquals(this.winner.getUsername(),
                leagueDto.lastWinners().stream().filter(n -> n.equals(this.winner.getUsername())).findFirst().get());
        assertEquals(this.p1.getUsername(),
                leagueDto.lastWinners().stream().filter(n -> n.equals(this.p1.getUsername())).findFirst().get());
    }

    private Participant generateParticipant(final String name) {
        return new Participant(null, name + "@example.org", new byte[] {}, name, false, List.of(), "2286", 0,
                Region.BURGENLAND, LocalDateTime.now(), List.of(), List.of(), List.of());
    }

    private void flush() {
        this.entityManager.flush();
        this.entityManager.clear();
    }
}
