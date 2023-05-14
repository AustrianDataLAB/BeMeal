package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.service.CommunityIdentificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/community-identification")
public class CommunityIdentificationEndpoint {

    private final CommunityIdentificationService communityIdentificationService;

    @PostMapping("/reload")
    @ResponseStatus(HttpStatus.CREATED)
    public void reloadCommunityIdentifications() {
        this.communityIdentificationService.reloadCommunityIdentifications();
    }
}
