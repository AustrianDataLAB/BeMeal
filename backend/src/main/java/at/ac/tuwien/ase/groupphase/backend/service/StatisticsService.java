package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.HeatMapDto;
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
    public HeatMapDto getStatistics(final HeatMapDto.Type type, final boolean relative, final int granularity) {
        Map<Long, Double> heatMapData;
        if (type.equals(HeatMapDto.Type.RANDOM)) {
            heatMapData = LongStream.range(10000, 100000).boxed()
                    .collect(Collectors.toMap(l -> l, l -> this.random.nextDouble(100000)));

        } else {
            heatMapData = new HashMap<>();
        }
        if (type.equals(HeatMapDto.Type.USER_BASE)) {
            this.communityIdentificationRepository.findAll().forEach(
                    c -> heatMapData.put(c.getCommunityIdentificationNumber(), (double) c.getParticipants().size()));
        }
        if (type.equals(HeatMapDto.Type.SUBMISSIONS)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(), (double) c.getParticipants()
                            .stream().map(p -> p.getSubmissions().size()).reduce(Integer::sum).orElse(0)));
        }
        if (type.equals(HeatMapDto.Type.VOTES)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(), (double) c.getParticipants()
                            .stream().map(p -> p.getVotes().size()).reduce(Integer::sum).orElse(0)));
        }
        if (type.equals(HeatMapDto.Type.WINS)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(),
                            (double) c.getParticipants().stream()
                                    .map(p -> p.getWins().values().stream().reduce(Integer::sum).orElse(0))
                                    .reduce(Integer::sum).orElse(0)));
        }
        if (type.equals(HeatMapDto.Type.UP_VOTES)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(),
                            (double) c.getParticipants().stream()
                                    .map(p -> p.getVotes().stream().filter(ParticipantSubmissionVote::isUpvote).count())
                                    .reduce(Long::sum).orElse(0L)));
        }
        if (type.equals(HeatMapDto.Type.DOWN_VOTES)) {
            this.communityIdentificationRepository.findAll()
                    .forEach(c -> heatMapData.put(c.getCommunityIdentificationNumber(),
                            (double) c.getParticipants().stream()
                                    .map(p -> p.getVotes().stream().filter(v -> !v.isUpvote()).count())
                                    .reduce(Long::sum).orElse(0L)));
        }
        if (type.equals(HeatMapDto.Type.USERNAME)) {
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
            return HeatMapDto.createRelative(normalize(groupedData), normalize(groupedUserMap), type);
        }
        return HeatMapDto.createAbsolute(normalize(groupedData), type);
    }

    private static Map<Long, Double> groupByGranularity(final Map<Long, Double> data, final int granularity) {
        return data.entrySet().stream()
                .collect(Collectors.groupingBy(entry -> entry.getKey() / ((long) Math.pow(10, 5 - granularity)),
                        Collectors.summingDouble(Map.Entry::getValue)));
    }

    public static Map<Long, Double> normalize(final Map<Long, Double> data) {
        final var minimum = 0L;
        final var maximum = 100000L;
        return LongStream.range(minimum, maximum).boxed()
                .collect(Collectors.toMap(k -> k, k -> data.getOrDefault(k, 0.0)));
    }
}
