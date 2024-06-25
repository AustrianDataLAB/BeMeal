package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.dto.WinningSubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.exception.ForbiddenAccessException;
import at.ac.tuwien.ase.groupphase.backend.mapper.SubmissionMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final Logger logger = LoggerFactory.getLogger(SubmissionService.class);
    private static final int MAX_WIDTH_HEIGHT = 1000;
    private static final String IMAGE_FORMAT = "png";
    private static final String IMAGE_PATH = "src/main/resources/static/img/";

    private final ParticipantRepository participantRepository;
    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;

    private final SubmissionMapper submissionMapper;
    private final ImageRepository imageRepository;

    @Transactional(rollbackFor = Exception.class, transactionManager = "rdbmsTxManager")
    public void submit(MultipartFile file, @NotNull String challengeId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Participant participant = this.participantRepository.findByUsername(username);
        Challenge challenge = this.challengeRepository.findById(Long.valueOf(challengeId)).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        UUID newUUID = UUID.randomUUID();

        // verify that the participant is eligible to submit to this challenge
        this.verifyEligibility(participant, challenge, now);

        // transform and save image
        imageRepository.save(file, newUUID);

        // add/overwrite submission
        Submission newSubmission = this.getNewSubmission(newUUID, now, participant, challenge);
        this.submissionRepository.save(newSubmission);

        List<Submission> submissions = participant.getSubmissions();
        // List<Submission> submissions =new ArrayList<>(new HashSet<>(participant.getSubmissions()));
        Submission previousSubmission = submissions.stream()
                .filter(x -> Long.valueOf(challengeId).equals(x.getChallenge().getId())).findAny().orElse(null);
        if (previousSubmission != null) {
            submissions.remove(previousSubmission);
            this.submissionRepository.delete(previousSubmission);
            this.imageRepository.delete(previousSubmission.getPicture());
        }
        submissions.add(newSubmission);
        participant.setSubmissions(submissions);
    }

    private Submission getNewSubmission(UUID uuid, LocalDateTime localDateTime, Participant participant,
            Challenge challenge) {
        Submission submission = new Submission();
        submission.setPicture(uuid);
        submission.setDate(localDateTime);
        submission.setParticipant(participant);
        submission.setChallenge(challenge);
        submission.setUpVotes(new ArrayList<>());
        return submission;
    }

    private void verifyEligibility(Participant participant, Challenge challenge, LocalDateTime now) {
        // check challenge still open for submissions
        if (now.isAfter(challenge.getEndDate().atTime(LocalTime.MAX))) {
            throw new ForbiddenAccessException("Challenge is closed for submissions");
        }

        // check challenge still open for submissions
        if (now.isBefore(challenge.getStartDate().atTime(LocalTime.MIN))) {
            throw new ForbiddenAccessException("Challenge is not open for submissions yet");
        }

        // check participant part of league
        if (!participant.getLeagues().contains(challenge.getLeague())) {
            throw new ForbiddenAccessException("Participant is not part of league");
        }
    }

    private static Path getPath(UUID uuid) {
        return Paths.get(IMAGE_PATH, uuid + "." + IMAGE_FORMAT);
    }

    /**
     * Resize BufferedImage.
     *
     * @param originalImage
     *            the original image
     * @param targetWidth
     *            the target width
     * @param targetHeight
     *            the target height
     *
     * @return the resized buffered image
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    @Transactional("rdbmsTxManager")
    public SubmissionDto getSubmission(@NotNull String submissionId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Participant participant = this.participantRepository.findByUsername(username);
        Submission submission = this.submissionRepository.findById(Long.valueOf(submissionId)).orElseThrow();

        // check if participant is allowed to see submission
        if (!participant.getLeagues().contains(submission.getChallenge().getLeague())) {
            throw new ForbiddenAccessException("Participant is not eligible to view submission");
        }

        return this.buildSubmissionDto(submission);
    }

    @Transactional("rdbmsTxManager")
    public SubmissionDto getCurrentSubmission(String challengeId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Participant participant = this.participantRepository.findByUsername(username);
        Submission submission = this.submissionRepository.getcurrentSubmission(Long.valueOf(challengeId),
                participant.getId());

        if (submission == null)
            return null;

        // check if participant is allowed to see submission
        if (!participant.getLeagues().contains(submission.getChallenge().getLeague())) {
            throw new ForbiddenAccessException("Participant is not eligible to view submission");
        }

        return this.buildSubmissionDto(submission);
    }

    private SubmissionDto buildSubmissionDto(Submission submission) {
        SubmissionDto submissionDto = this.submissionMapper.submissionToSubmissionDto(submission);
        UUID uuid = submission.getPicture();
        addPicture(submissionDto, uuid);
        return submissionDto;
    }

    public WinningSubmissionDto buildWinningSubmissionDto(Submission submission) {
        WinningSubmissionDto submissionDto = this.submissionMapper.submissionToWinningSubmissionDto(submission);
        submissionDto.setParticipantName(submission.getParticipant().getUsername());
        submissionDto.setChallengeId(submission.getChallenge().getId());
        UUID uuid = submission.getPicture();
        addPicture(submissionDto, uuid);
        return submissionDto;
    }

    private void addPicture(SubmissionDto submissionDto, UUID uuid) {
        logger.trace("addPicture({}, {})", submissionDto, uuid);
        try {
            byte[] bytes = Files.readAllBytes(getPath(uuid));
            String imageString = Base64.getEncoder().withoutPadding().encodeToString(bytes);
            submissionDto.setPicture(imageString);
        } catch (NoSuchFileException nsfe) {
            logger.warn("File {} of submission with id {} could not be found.", getPath(uuid), submissionDto.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional("rdbmsTxManager")
    public void saveVote(Long submissionId, String participantUsername, boolean isUpvote) {
        logger.trace("saveVote({}, {}, {})", submissionId, participantUsername, isUpvote);
        Participant user = (Participant) this.userRepository.findByUsername(participantUsername);
        Submission submission = this.submissionRepository.findById(submissionId).orElseThrow();

        ParticipantSubmissionVote vote = new ParticipantSubmissionVote(user, submission, isUpvote);
        voteRepository.save(vote);
    }

    @Transactional("rdbmsTxManager")
    public List<SubmissionDto> getNotVotedSubmissionsOfUser(Long challengeId, String participantUsername) {
        logger.trace("getNotVotedSubmissionsOfUser({}, {})", challengeId, participantUsername);
        Participant user = (Participant) this.userRepository.findByUsername(participantUsername);

        List<Submission> submissions = submissionRepository.getSubmissionNotUpvotedYetByUser(challengeId, user.getId());

        return submissions.stream().map(this::buildSubmissionDto).collect(Collectors.toList());
    }

    @Transactional("rdbmsTxManager")
    public List<SubmissionWithUpvotes> getWinningSubmissionForChallange(Long challengeId) {
        logger.trace("getWinningSubmissionForChallange({})", challengeId);
        List<SubmissionWithUpvotes> sub = submissionRepository.getWinnerSubmissionOfChallenge(challengeId);

        if (sub == null || sub.isEmpty()) {
            logger.debug("No upvotes for submissions in challenge with id {}", challengeId);
            return List.of();
        }

        // Only return upvotes, that have more than 0 upvotes
        return sub.stream().filter(s -> s.getUpvotes() > 0).collect(Collectors.toList());
    }
}
