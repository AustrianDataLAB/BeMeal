package at.ac.tuwien.ase.groupphase.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record HeatMap(List<HeatMapEntry> entries, Type type, boolean relative) {

    public enum Type {
        RANDOM, USER_BASE, SUBMISSIONS, VOTES, WINS, UP_VOTES, DOWN_VOTES, USERNAME
    }

    public record HeatMapEntry(long id, double rate) {
    }

    public static HeatMap createAbsolute(final Map<Long, Double> data, final Type type) {
        return create(data, type, false);
    }

    public static HeatMap createRelative(final Map<Long, Double> data, final Map<Long, Double> userMap,
            final Type type) {
        final var relativeData = data.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / userMap.getOrDefault(e.getKey(), 1.0)));
        return create(relativeData, type, true);
    }

    private static HeatMap create(final Map<Long, Double> data, final Type type, final boolean relative) {
        final var entries = data.entrySet().stream().map(e -> new HeatMapEntry(e.getKey(), e.getValue())).toList();
        return new HeatMap(entries, type, relative);
    }
}
