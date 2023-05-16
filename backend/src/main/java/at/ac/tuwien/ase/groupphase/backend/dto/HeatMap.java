package at.ac.tuwien.ase.groupphase.backend.dto;

import java.util.Map;
import java.util.stream.Collectors;

public record HeatMap(Map<Long, Double> data, Type type, boolean relative) {

    public HeatMap toRelative() {
        final var maximum = this.data.values().stream().max(Double::compareTo)
                .orElseThrow(() -> new RuntimeException("Found no maximum"));
        final var relativeData = this.data.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / maximum));
        return new HeatMap(relativeData, this.type, true);
    }

    public enum Type {
        RANDOM
    }
}
