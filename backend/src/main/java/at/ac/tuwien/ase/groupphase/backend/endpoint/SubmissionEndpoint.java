package at.ac.tuwien.ase.groupphase.backend.endpoint;

import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.exception.ForbiddenAccessException;
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
        this.submissionService.submit(file, challengeId);
    }

    @GetMapping("/{submissionId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public SubmissionDto getSubmission(@NotNull @PathVariable final String submissionId) {
        logger.trace("getSubmission({})", submissionId);
        return this.submissionService.getSubmission(submissionId);
    }

    @GetMapping("/current/{challengeId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public SubmissionDto getCurrentSubmission(@NotNull @PathVariable final String challengeId) {
        logger.trace("getCurrentSubmission({})", challengeId);
        return this.submissionService.getCurrentSubmission(challengeId);
    }

    @GetMapping("/upvotes/{challengeId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public List<SubmissionDto> getUpvoteSubmissions(@NotNull @PathVariable final Long challengeId) {
        logger.trace("getUpvoteSubmissions({})", challengeId);
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return submissionService.getNotVotedSubmissionsOfUser(challengeId, username);
    }

    @PostMapping("/upvote/{submissionId}/{isUpvote}")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerToken")
    public void upvoteSubmission(@NotNull @PathVariable final Long submissionId,
            @NotNull @PathVariable final boolean isUpvote) {
        logger.trace("upvoteSubmission({},{})", submissionId, isUpvote);
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        submissionService.saveVote(submissionId, username, isUpvote);
    }

}
