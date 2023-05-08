package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.exception.ForbiddenAccessException;
import at.ac.tuwien.ase.groupphase.backend.service.SubmissionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/submission")
public class SubmissionEndpoint {

    private final SubmissionService submissionService;

    @PostMapping("/submit/{challengeId}")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerToken")
    public void submit(@RequestParam("file") MultipartFile file, @NotNull @PathVariable final String challengeId) {
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
        try {
            return this.submissionService.getSubmission(submissionId);
        } catch (ForbiddenAccessException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Oops");
        }
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public List<SubmissionDto> getUpvoteSubmissions() {
        try {
            // todo @Manu impl this correctly, get / :all submissions that can be upvoted by a user: do not return if
            // date is invalid or user has already upvoted it
            List<SubmissionDto> submissions = new ArrayList<>();
            // for (int i = 1; i < 7; i++) {
            // try {
            // submissions.add(getSubmission(String.valueOf(i)));
            // } catch (ResponseStatusException e) {
            // }
            // }
            return submissions;
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
    public void upvoteSubmission(@NotNull @PathVariable final String submissionId,
            @NotNull @PathVariable final String isUpvote) {
        try {
            // todo @Manu upvote a submission, get user from context
            // isUpvote: true --> swipe right, upvote. false --> swipe left, has to be saved in db too. otherwise the
            // disliked submissions will appear
            // again and again for user!
            System.out.println("wuff wuff");
        } catch (ForbiddenAccessException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Oops");
        }
    }

}
