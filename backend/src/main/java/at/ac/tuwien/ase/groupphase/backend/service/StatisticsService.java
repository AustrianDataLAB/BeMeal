package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.HeatMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {

    private final Random random = new Random();

    public HeatMap getStatistics(final HeatMap.Type type, final boolean relative) {
        if (type.equals(HeatMap.Type.RANDOM)) {
            final var data = LongStream.range(10000, 100000).boxed()
                    .collect(Collectors.toMap(l -> l, l -> this.random.nextDouble(100000)));
            final var heatMap = new HeatMap(data, HeatMap.Type.RANDOM, false);
            if (relative) {
                return heatMap.toRelative();
            }
            return heatMap;
        }
        return null;
    }
}
