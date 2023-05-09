package at.ac.tuwien.ase.groupphase.backend.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SubmissionService {

    /*
    TODO
    - handle file submission
        - verify user
        - verify challengeId
        - check that participant is eligible to submit for this challenge --> i.e., part of the respective league?
        - check if challenge still open for submissions
        - check if participant already submitted --> update/override previous image
        - handle image
            - check size --> additionally to max file size option downscale so that all images have the same dimensions?
            - generate UUID
            - save file to disk
            - insert/override UUID into submission DB
     */

    public void submit(MultipartFile file, @NotNull String challengeId) {

    }

}
