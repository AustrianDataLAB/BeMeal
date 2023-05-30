package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.HeatMap;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.ParticipantSubmissionVote;
import at.ac.tuwien.ase.groupphase.backend.repository.CommunityIdentificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {

    private final Random random = new Random();

    private final CommunityIdentificationRepository communityIdentificationRepository;

    @Transactional
    public HeatMap getStatistics(final HeatMap.Type type, final boolean relative, final int granularity) {
        Map<Long, Double> heatMapData;
        if (type.equals(HeatMap.Type.RANDOM)) {
            heatMapData = LongStream.range(10000, 100000).boxed()
                    .collect(Collectors.toMap(l -> l, l -> this.random.nextDouble(100000)));

        } else {
            heatMapData = new HashMap<>();
        }
        if (type.equals(HeatMap.Type.USER_BASE)) {
            this.communityIdentificationRepository.findAll().forEach(
                    c -> heatMapData.put(c.getCommunityIdentificationNumber(), (double) c.getParticipants().size()));
        }
        if (type.equals(HeatMap.Type.SUBMISSIONS)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(), (double) c.getParticipants()
                            .stream().map(p -> p.getSubmissions().size()).reduce(Integer::sum).orElse(0)));
        }
        if (type.equals(HeatMap.Type.VOTES)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(), (double) c.getParticipants()
                            .stream().map(p -> p.getVotes().size()).reduce(Integer::sum).orElse(0)));
        }
        if (type.equals(HeatMap.Type.WINS)) {
            this.communityIdentificationRepository.findAll().forEach(c -> heatMapData.put(
                    c.getCommunityIdentificationNumber(),
                    (double) c.getParticipants().stream().map(Participant::getWins).reduce(Integer::sum).orElse(0)));
        }
        if (type.equals(HeatMap.Type.UP_VOTES)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(),
                            (double) c.getParticipants().stream()
                                    .map(p -> p.getVotes().stream().filter(ParticipantSubmissionVote::isUpvote).count())
                                    .reduce(Long::sum).orElse(0L)));
        }
        if (type.equals(HeatMap.Type.DOWN_VOTES)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(),
                            (double) c.getParticipants().stream()
                                    .map(p -> p.getVotes().stream().filter(v -> !v.isUpvote()).count())
                                    .reduce(Long::sum).orElse(0L)));
        }
        if (type.equals(HeatMap.Type.USERNAME)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(), (double) c.getParticipants()
                            .stream().map(p -> p.getUsername().length()).reduce(Integer::sum).orElse(0)));
        }
        final var groupedData = groupByGranularity(heatMapData, granularity);
        final var userMap = new HashMap<Long, Double>();
        this.communityIdentificationRepository.findAll()
                .forEach(c -> userMap.put(c.getCommunityIdentificationNumber(), (double) c.getParticipants().size()));
        final var groupedUserMap = groupByGranularity(userMap, granularity);
        if (relative) {
            return HeatMap.createRelative(groupedData, groupedUserMap, type);
        }
        return HeatMap.createAbsolute(groupedData, type);
    }

    private static Map<Long, Double> groupByGranularity(final Map<Long, Double> data, final int granularity) {
        return data.entrySet().stream()
                .collect(Collectors.groupingBy(entry -> entry.getKey() / ((long) Math.pow(10, 5 - granularity)),
                        Collectors.summingDouble(Map.Entry::getValue)));
    }
}
