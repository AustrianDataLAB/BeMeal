package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.entity.CommunityIdentification;
import at.ac.tuwien.ase.groupphase.backend.repository.CommunityIdentificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityIdentificationService {

    private static final String MAPPING_PATH = "src/main/resources/ci-postal-mapping.csv";
    private final CommunityIdentificationRepository communityIdentificationRepository;

    /**
     * Reload all community identifications from the disk. The content will be taken from
     * `src/main/resources/ci-postal-mapping.csv`. The old community identifications will be removed.
     */
    @Transactional
    public void reloadCommunityIdentifications() {
        this.communityIdentificationRepository.deleteAll();
        try (final var mappingReader = new BufferedReader(new FileReader(MAPPING_PATH))) {
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
