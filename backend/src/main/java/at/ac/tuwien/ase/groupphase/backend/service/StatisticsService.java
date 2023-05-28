package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.HeatMap;
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
        final var groupedData = heatMapData.entrySet().stream()
                .collect(Collectors.groupingBy(entry -> entry.getKey() / ((long) Math.pow(10, 5 - granularity)),
                        Collectors.summingDouble(Map.Entry::getValue)));
        if (relative) {
            return HeatMap.createRelative(groupedData, type);
        }
        return HeatMap.createAbsolute(groupedData, type);
    }
}
