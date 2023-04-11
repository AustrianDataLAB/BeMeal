package at.ac.tuwien.ase.groupphase.backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/league")
public class LeagueEndpoint {

    private final Logger logger = LoggerFactory.getLogger(LeagueEndpoint.class);

    @GetMapping("/test")
    @SecurityRequirement(name = "bearerToken")
    @ResponseStatus(HttpStatus.OK)
    public String test() {
        return "junge";
    }
}
