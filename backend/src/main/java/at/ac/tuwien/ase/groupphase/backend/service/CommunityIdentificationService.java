package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.BackendApplication;
import at.ac.tuwien.ase.groupphase.backend.entity.CommunityIdentification;
import at.ac.tuwien.ase.groupphase.backend.repository.CommunityIdentificationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityIdentificationService {

    private static final String MAPPING_PATH = "ci-postal-mapping.csv";
    private final CommunityIdentificationRepository communityIdentificationRepository;

    /**
     * Reload all community identifications from the disk. The content will be taken from
     * `src/main/resources/ci-postal-mapping.csv`. The old community identifications will be removed.
     */
    @Transactional("rdbmsTxManager")
    @PostConstruct
    public void reloadCommunityIdentifications() {
        if (this.communityIdentificationRepository.count() > 0) {
            log.info("There is a mapping present, reload is aborted");
            return;
        }
        try (final var mappingReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(Objects
                .requireNonNull(BackendApplication.class.getClassLoader().getResource(MAPPING_PATH)).openStream())))) {
            final var communityIdentifications = mappingReader.lines().map(l -> {
                final var parts = l.split(",");
                final var communityIdentificationNumber = Long.parseLong(parts[0]);
                return new CommunityIdentification(communityIdentificationNumber, parts[1], Set.of());
            }).collect(Collectors.toSet());
            final var communityIdentificationsWithUniquePostal = new HashSet<CommunityIdentification>();
            communityIdentifications.forEach(c -> {
                if (communityIdentificationsWithUniquePostal.stream()
                        .noneMatch(c2 -> c2.getPostalCode().equals(c.getPostalCode()))) {
                    communityIdentificationsWithUniquePostal.add(c);
                }
            });
            this.communityIdentificationRepository.saveAll(communityIdentificationsWithUniquePostal);
            log.info("Reloaded the postcode -> community identification ISO mapping from disk");
        } catch (IOException e) {
            log.error("Unable to reload postcode mapping", e);
        }
    }
}
