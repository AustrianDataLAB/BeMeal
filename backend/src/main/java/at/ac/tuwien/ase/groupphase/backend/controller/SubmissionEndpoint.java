package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.service.SubmissionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/submission")
public class SubmissionEndpoint {

    private final SubmissionService submissionService;

    @PostMapping("/submit/{challengeId}")
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerToken")
    public void submit(@RequestParam("file") MultipartFile file, @NotNull @PathVariable final String challengeId) {
        this.submissionService.submit(file, challengeId);
        // TODO response / return ?
    }

    @GetMapping("/{submissionId}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerToken")
    public SubmissionDto getSubmission(@NotNull @PathVariable final String submissionId) {
        return this.submissionService.getSubmission(submissionId);
    }

}
