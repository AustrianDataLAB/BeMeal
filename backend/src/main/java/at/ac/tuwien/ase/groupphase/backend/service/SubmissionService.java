package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.Submission;
import at.ac.tuwien.ase.groupphase.backend.exception.ForbiddenAccessException;
import at.ac.tuwien.ase.groupphase.backend.mapper.SubmissionMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.SubmissionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SubmissionService {

    private static final int MAX_WIDTH_HEIGHT = 1000;
    private static final String IMAGE_FORMAT = "png";
    private static final String IMAGE_PATH = "src/main/resources/static/img/";

    private final ParticipantRepository participantRepository;
    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;

    private final SubmissionMapper submissionMapper;

    @Autowired
    public SubmissionService(ParticipantRepository participantRepository, ChallengeRepository challengeRepository,
                             SubmissionRepository submissionRepository, SubmissionMapper submissionMapper) {
        this.participantRepository = participantRepository;
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
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

        ////////////////////////////////////////////////////////////////
        // verify that the participant is eligible to submit to this challenge
        this.verifyEligibility(participant, challenge, now);

        ////////////////////////////////////////////////////////////////
        // transform and save image
        try {
            InputStream inputStream = file.getInputStream();
            BufferedImage originalImage = ImageIO.read(inputStream);
            inputStream.close();

            int[] correctWidthHeight = widthHeightCorrectAspectRatio(originalImage);
            BufferedImage resizedImage = resizeImage(originalImage, correctWidthHeight[0], correctWidthHeight[1]);

            File f = new File(getPath(newUUID).toUri());

            ImageIO.write(resizedImage, IMAGE_FORMAT, f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ////////////////////////////////////////////////////////////////
        // add/overwrite submission
        Submission newSubmission = this.getNewSubmission(newUUID, now, participant, challenge);
        this.submissionRepository.save(newSubmission);

        List<Submission> submissions = participant.getSubmissions();
        Submission previousSubmission = submissions.stream()
                .filter(x -> Long.valueOf(challengeId).equals(x.getChallenge().getId())).findAny().orElse(null);
        if (previousSubmission != null) {
            submissions.remove(previousSubmission);
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
     * @param img the img
     * @return res array: width=res[0] height=res[1]
     */
    private static int[] widthHeightCorrectAspectRatio(BufferedImage img) {
        // LOGGER.info("original width and height: width: " + img.getWidth() + " height: " + img.getHeight());
        int[] res = {img.getWidth(), img.getHeight()};
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
     * @param originalImage the original image
     * @param targetWidth   the target width
     * @param targetHeight  the target height
     * @return the resized buffered image
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    /**
     * Convert BufferedImage to byte array.
     *
     * @param bi the BufferedImage
     * @return the byte array
     */
    private static byte[] bufferedImageToByteArray(BufferedImage bi) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bi, SubmissionService.IMAGE_FORMAT, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        SubmissionDto submissionDto = this.submissionMapper.submissionToSubmissionDto(submission);

        UUID uuid = submission.getPicture();

        try {
            byte[] bytes = Files.readAllBytes(getPath(uuid));
            submissionDto.setPicture(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return submissionDto;
    }
}
