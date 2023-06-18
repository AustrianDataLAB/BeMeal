package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.dto.WinningSubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.ParticipantSubmissionVote;
import at.ac.tuwien.ase.groupphase.backend.entity.Submission;
import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.exception.ForbiddenAccessException;
import at.ac.tuwien.ase.groupphase.backend.mapper.SubmissionMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor
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

    @Autowired
    public SubmissionService(ParticipantRepository participantRepository, ChallengeRepository challengeRepository,
            SubmissionRepository submissionRepository, VoteRepository voteRepository, UserRepository userRepository,
            SubmissionMapper submissionMapper) {
        this.participantRepository = participantRepository;
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.submissionMapper = submissionMapper;
    }

    /*
     * TODO - handle file submission - verify user - verify challengeId - check that participant is eligible to submit
     * for this challenge --> i.e., part of the respective league? - check if challenge still open for submissions -
     * check if participant already submitted --> update/override previous image - handle image - check size -->
     * additionally to max file size option downscale so that all images have the same dimensions? - generate UUID -
     * save file to disk - insert/override UUID into submission DB
     */

    @Transactional(rollbackOn = Exception.class)
    public void submit(MultipartFile file, @NotNull String challengeId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Participant participant = this.participantRepository.findByUsername(username);
        Challenge challenge = this.challengeRepository.findById(Long.valueOf(challengeId)).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        UUID newUUID = UUID.randomUUID();

        // verify that the participant is eligible to submit to this challenge
        this.verifyEligibility(participant, challenge, now);

        // transform and save image
        try {
            InputStream inputStream = file.getInputStream();
            BufferedImage originalImage = ImageIO.read(inputStream); // javax.imageio has built-in support for GIF, PNG,
                                                                     // JPEG, BMP, and WBMP
            inputStream.close();

            int[] correctWidthHeight = widthHeightCorrectAspectRatio(originalImage);
            BufferedImage resizedImage = resizeImage(originalImage, correctWidthHeight[0], correctWidthHeight[1]);

            File f = new File(getPath(newUUID).toUri());

            ImageIO.write(resizedImage, IMAGE_FORMAT, f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // add/overwrite submission
        Submission newSubmission = this.getNewSubmission(newUUID, now, participant, challenge);
        this.submissionRepository.save(newSubmission);

        List<Submission> submissions = participant.getSubmissions();
        Submission previousSubmission = submissions.stream()
                .filter(x -> Long.valueOf(challengeId).equals(x.getChallenge().getId())).findAny().orElse(null);
        if (previousSubmission != null) {
            submissions.remove(previousSubmission);
            this.submissionRepository.delete(previousSubmission);
            try {
                Files.delete(getPath(previousSubmission.getPicture()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
     * Calculates the width and height with the correct aspect ratio.
     *
     * @param img
     *            the img
     *
     * @return res array: width=res[0] height=res[1]
     */
    private static int[] widthHeightCorrectAspectRatio(BufferedImage img) {
        int[] res = { img.getWidth(), img.getHeight() };
        if (img.getWidth() > SubmissionService.MAX_WIDTH_HEIGHT
                || img.getHeight() > SubmissionService.MAX_WIDTH_HEIGHT) {
            if (img.getWidth() >= img.getHeight()) {
                res[0] = SubmissionService.MAX_WIDTH_HEIGHT;
                res[1] = (int) (((0.0 + img.getHeight()) / img.getWidth()) * SubmissionService.MAX_WIDTH_HEIGHT);
            } else {
                res[0] = (int) (((0.0 + img.getWidth()) / img.getHeight()) * SubmissionService.MAX_WIDTH_HEIGHT);
                res[1] = SubmissionService.MAX_WIDTH_HEIGHT;
            }
        }
        return res;
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

    @Transactional
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

    @Transactional
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
        try {
            byte[] bytes = Files.readAllBytes(getPath(uuid));
            String imageString = Base64.getEncoder().withoutPadding().encodeToString(bytes);
            submissionDto.setPicture(imageString);
        } catch (NoSuchFileException nsfe) {
            logger.info("File {} of submission with id {} could not be found.", getPath(uuid), submissionDto.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void saveVote(Long submissionId, String participantUsername, boolean isUpvote) {
        logger.trace("saveVote({}, {}, {})", submissionId, participantUsername, isUpvote);
        Participant user = (Participant) this.userRepository.findByUsername(participantUsername);
        Submission submission = this.submissionRepository.findById(submissionId).orElseThrow();

        ParticipantSubmissionVote vote = new ParticipantSubmissionVote(user, submission, isUpvote);
        voteRepository.save(vote);
    }

    public List<SubmissionDto> getNotVotedSubmissionsOfUser(Long challengeId, String participantUsername) {
        logger.trace("getNotVotedSubmissionsOfUser({}, {})", challengeId, participantUsername);
        Participant user = (Participant) this.userRepository.findByUsername(participantUsername);

        List<Submission> submissions = submissionRepository.getSubmissionNotUpvotedYetByUser(challengeId, user.getId());

        return submissions.stream().map(this::buildSubmissionDto).collect(Collectors.toList());
    }

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
