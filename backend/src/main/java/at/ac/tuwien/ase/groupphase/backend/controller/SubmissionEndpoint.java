package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.exception.ForbiddenAccessException;
import at.ac.tuwien.ase.groupphase.backend.service.ParticipantService;
import at.ac.tuwien.ase.groupphase.backend.service.SubmissionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/submission")
public class SubmissionEndpoint {

    private final Logger logger = LoggerFactory.getLogger(SubmissionEndpoint.class);
    private final SubmissionService submissionService;

    @PostMapping("/submit/{challengeId}")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerToken")
    public void submit(@RequestParam("file") MultipartFile file, @NotNull @PathVariable final String challengeId) {
        logger.trace("submit({}, {})", file, challengeId);

        try {
            this.submissionService.submit(file, challengeId);
        } catch (ForbiddenAccessException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Oops");
        }
    }

    @GetMapping("/{submissionId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public SubmissionDto getSubmission(@NotNull @PathVariable final String submissionId) {
        logger.trace("getSubmission({})", submissionId);

        try {
            return this.submissionService.getSubmission(submissionId);
        } catch (ForbiddenAccessException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Oops");
        }
    }

    @GetMapping("/current/{challengeId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public SubmissionDto getCurrentSubmission(@NotNull @PathVariable final String challengeId) {
        logger.trace("getCurrentSubmission({})", challengeId);

        try {
            return this.submissionService.getCurrentSubmission(challengeId);
        } catch (ForbiddenAccessException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Oops");
        }
    }

    @GetMapping("/upvotes/{challengeId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public List<SubmissionDto> getUpvoteSubmissions(@NotNull @PathVariable final Long challengeId) {
        logger.trace("getUpvoteSubmissions({})", challengeId);

        try {
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            return submissionService.getNotVotedSubmissionsOfUser(challengeId, username);
        } catch (ForbiddenAccessException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Oops");
        }
    }

    @PostMapping("/upvote/{submissionId}/{isUpvote}")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerToken")
    public void upvoteSubmission(@NotNull @PathVariable final Long submissionId,
            @NotNull @PathVariable final boolean isUpvote) {
        logger.trace("upvoteSubmission({},{})", submissionId, isUpvote);

        try {
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            submissionService.saveVote(submissionId, username, isUpvote);
        } catch (ForbiddenAccessException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Oops");
        }
    }

}
