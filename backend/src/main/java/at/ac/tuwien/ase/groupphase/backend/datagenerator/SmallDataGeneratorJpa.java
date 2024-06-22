package at.ac.tuwien.ase.groupphase.backend.datagenerator;

import at.ac.tuwien.ase.groupphase.backend.entity.*;
import at.ac.tuwien.ase.groupphase.backend.repository.ChallengeRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.LeagueRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.SchemaInformationRepository;
import at.ac.tuwien.ase.groupphase.backend.service.CommunityIdentificationService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@Profile("SmallDataGeneratorJpa")
@AllArgsConstructor
public class SmallDataGeneratorJpa {
    private final Logger logger = LoggerFactory.getLogger(SmallDataGeneratorJpa.class);

    private CommunityIdentificationService communityIdentificationService;

    private SchemaInformationRepository schemaInformationRepository;

    private LeagueRepository leagueRepository;
    private ParticipantRepository participantRepository;
    private ChallengeRepository challengeRepository;

    @PostConstruct
    @Transactional("rdbmsTxManager")
    public void insert() {
        final var schemaInformationOpt = this.schemaInformationRepository.getSchemaInformation();

        if (schemaInformationOpt.isPresent()) {
            final var si = schemaInformationOpt.get();
            if (si.isInitialized()) {
                this.logger.info("Schema data is already initialized by {}, skip initialization", si.getGenerator());
                return;
            } else {
                this.logger.warn("Schema is not initialized but entry is set by {}", si.getGenerator());
            }
        }

        final var leagues = this.leagues();
        final var regionalLeagues = this.leagues().stream().filter(l -> l.getId() <= 8).collect(Collectors.toSet());
        final var nonRegionalLeagues = leagues.stream().filter(l -> l.getId() > 8).collect(Collectors.toSet());
        final var participants = this.participants();
        final var participantsByRegion = participants.stream().collect(Collectors.groupingBy(Participant::getRegion));

        regionalLeagues.forEach(l -> l.setParticipants(participantsByRegion.get(l.getRegion())));
        nonRegionalLeagues.forEach(l -> l.setParticipants(participants.stream()
                .filter(p -> p.getId() % l.getId() == 0 || p.getId() * p.getId() % l.getId() == 0
                        || p.getId() * p.getId() * p.getId() % l.getId() == 0)
                .collect(Collectors.toSet()).stream().toList()));

        final var regionalChallenges = regionalLeagues.stream()
                .map(l -> new Challenge(null, "Weekly Challenge for " + l.getName(), LocalDate.now(),
                        LocalDate.now().plusDays(7), "96750", List.of(), l))
                .collect(Collectors.toSet());
        final var nonRegionalChallenges = regionalLeagues.stream()
                .map(l -> new Challenge(null, "Weekly Challenge for " + l.getName(), LocalDate.now(),
                        LocalDate.now().plusDays(7), "102452", List.of(), l))
                .collect(Collectors.toSet());

        communityIdentificationService.reloadCommunityIdentifications();

        this.participantRepository.saveAllAndFlush(participants);
        this.leagueRepository.saveAll(leagues);
        this.challengeRepository.saveAll(regionalChallenges);
        this.challengeRepository.saveAll(nonRegionalChallenges);

        this.schemaInformationRepository.save(new SchemaInformation(this.getClass().getSimpleName()));

        this.logger.info("Schema initialization finished");
    }

    private Set<League> leagues() {
        return Set.of(
                new League(0L, GameMode.PICTURE_INGREDIENTS, Region.VIENNA, 7,
                        UUID.fromString("d9e7c7c7-0eab-42f6" + "-a09b-475a2bf08f66"), "Vienna League"),
                new League(1L, GameMode.PICTURE_INGREDIENTS, Region.LOWER_AUSTRIA, 7,
                        UUID.fromString("d9e7c7c7-0eab" + "-42f6-a09b-475a2bf08f66"), "Lower Austria League"),
                new League(2L, GameMode.PICTURE_INGREDIENTS, Region.UPPER_AUSTRIA, 7,
                        UUID.fromString("d9e7c7c7-0eab" + "-42f6-a09b-475a2bf08f66"), "Upper Austria League"),
                new League(3L, GameMode.PICTURE_INGREDIENTS, Region.STYRIA, 7,
                        UUID.fromString("d9e7c7c7-0eab-42f6" + "-a09b-475a2bf08f66"), "Styria League"),
                new League(4L, GameMode.PICTURE_INGREDIENTS, Region.TYROL, 7,
                        UUID.fromString("d9e7c7c7-0eab-42f6" + "-a09b-475a2bf08f66"), "Tyrol League"),
                new League(5L, GameMode.PICTURE_INGREDIENTS, Region.SALZBURG, 7,
                        UUID.fromString("d9e7c7c7-0eab-42f6" + "-a09b-475a2bf08f66"), "Salzburg League"),
                new League(6L, GameMode.PICTURE_INGREDIENTS, Region.CARINTHIA, 7,
                        UUID.fromString("d9e7c7c7-0eab-42f6" + "-a09b-475a2bf08f66"), "Carinthia League"),
                new League(7L, GameMode.PICTURE_INGREDIENTS, Region.BURGENLAND, 7,
                        UUID.fromString("d9e7c7c7-0eab" + "-42f6-a09b-475a2bf08f66"), "Burgenland League"),
                new League(8L, GameMode.PICTURE_INGREDIENTS, Region.VORARLBERG, 7,
                        UUID.fromString("d9e7c7c7-0eab" + "-42f6-a09b-475a2bf08f66"), "Vorarlberg League"),

                new League(10L, GameMode.PICTURE_INGREDIENTS, Region.VIENNA, 7,
                        UUID.fromString("ef2133ad-f5e9-4aac" + "-bc1f-46dcf62f495e"), "Uni friends"),
                new League(11L, GameMode.PICTURE_INGREDIENTS, Region.LOWER_AUSTRIA, 7,
                        UUID.fromString("df3fe84b-6143" + "-4d41-a0b9-a452c8c91b18"), "Work friends"),
                new League(12L, GameMode.PICTURE_INGREDIENTS, Region.UPPER_AUSTRIA, 7,
                        UUID.fromString("d964c968-8078" + "-40c7-b528-bb305ec7290a"), "The legends"),
                new League(13L, GameMode.PICTURE_INGREDIENTS, Region.STYRIA, 7,
                        UUID.fromString("8b7edb62-a5db-4c65" + "-9adb-c3a7d2fc9952"), "Cooking Experts"),
                new League(14L, GameMode.PICTURE_INGREDIENTS, Region.TYROL, 7,
                        UUID.fromString("300fdfc3-ebd1-4a5a" + "-bb56-fbad47ae91ce"), "Broccoli lovers"),
                new League(15L, GameMode.PICTURE_INGREDIENTS, Region.SALZBURG, 7,
                        UUID.fromString("8fe94524-e052-49f8" + "-a55d-3f4af99317bb"), "Heast, oida"),
                new League(16L, GameMode.PICTURE_INGREDIENTS, Region.CARINTHIA, 7,
                        UUID.fromString("645379dd-3b61" + "-4635-817d-1ee9808e20b0"), "Foodies"),
                new League(17L, GameMode.PICTURE_INGREDIENTS, Region.BURGENLAND, 7,
                        UUID.fromString("64188f31-9fc5" + "-478f-9342-4ccd61cc0920"), "True legends"),
                new League(18L, GameMode.PICTURE_INGREDIENTS, Region.VORARLBERG, 7,
                        UUID.fromString("d9e7c7c7-0eab" + "-42f6-a09b-475a2bf08f66"), "Turtles"));
    }

    private Set<Participant> participants() {
        return Set.of(
                new Participant(1L, "benjamin.probst@bemeal.at", "Bini",
                        "$2a$12$nsiO.0j8gKPbbLssvXxR5" + ".F6NuIX1BAb5Ll14LH.6MJHjxKuVvI/O", true, "2301",
                        Region.LOWER_AUSTRIA, LocalDateTime.parse("2020-05-20T23:14:37")),
                new Participant(2L, "dave.pfliegler@bemeal.at", "Dave",
                        "$2a$12$37Oc6dzwqWXR2Jn1mkCs" + ".uLOPkzpBUdj8M0bHtwYybBwSG.7Gj/OG", true, "2401",
                        Region.LOWER_AUSTRIA, LocalDateTime.parse("2020-05-20T23:14:37")),
                new Participant(3L, "dennis.toth@bemeal.at", "Dennis",
                        "$2a$10$DFYTkpABc.pTpJWCYMBvW" + ".Wijxwl3lr67G6I5r5Pzcr.5/p2V4xwm", true, "3804",
                        Region.LOWER_AUSTRIA, LocalDateTime.parse("2020-05-20T23:14:37")),
                new Participant(4L, "manuel.waibel@bemeal.at", "Manu",
                        "$2a$10$TOPbeItKkxyC2SAIw6DXN" + ".y725xGXG5Rxs8ZG6JaaZU..gDCU.DWy", true, "6890",
                        Region.VORARLBERG, LocalDateTime.parse("2020-05-20T23:14:37")),
                new Participant(5L, "matteo.kofler@bemeal.at", "Matteo",
                        "$2a$10$p61cLtBQ3h3vwXy2GB9c" + ".OJPX9ae3qp8SLNNmCxt0mZXPd.rWOicG", true, "6850",
                        Region.VORARLBERG, LocalDateTime.parse("2020-05-20T23:14:37")),
                new Participant(6L, "richard.stoeckl@bemeal.at", "Richi",
                        "$2a$10$diB3.0ugwJk" + ".7wdeAY0PBOU90Ukw4MQtOAvbMxnO.tsE7mtRsLv5C", true, "1010",
                        Region.LOWER_AUSTRIA, LocalDateTime.parse("2020-05-20T23:14:37")),

                new Participant(7L, "test1@gmail.com", "test1",
                        "$2a$10$qlE9fTpRBEsw1nBpFXvcPOPafSnlKKIfSNHMceKizTGdckSU1qbJi", false, "1100",
                        Region.LOWER_AUSTRIA, LocalDateTime.parse("2021-01-22T16:38:54")),
                new Participant(8L, "test2@gmail.com", "test2",
                        "$2a$10$2CUt" + "/N6l99GmxWmfBBXnDOTMueJDFOhKUdQDUyk7NQpxX4gmDVPpO", false, "6890",
                        Region.BURGENLAND, LocalDateTime.parse("2021-08-21T18:42:41")),
                new Participant(9L, "test3@gmail.com", "test3",
                        "$2a$10$sx/9lMzt/9s9QG.NvtvHf" + ".JbTdAqmRahMEjuNcAqOp47nO6uH.v0G", false, "2100",
                        Region.STYRIA, LocalDateTime.parse("2021" + "-07-02T21:32:05")),
                new Participant(10L, "test4@gmail.com", "test4",
                        "$2a$10$3QSmui3" + ".ul8jAWpXRzKw4eqSrO7822dM5qIecTFOYG4GHwXfHSNuO", false, "5020",
                        Region.BURGENLAND, LocalDateTime.parse("2020-05-20T23:14:37")));

    }
}
