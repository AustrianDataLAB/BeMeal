package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.HeatMap;
import at.ac.tuwien.ase.groupphase.backend.service.StatisticsService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsEndpoint {

    private final StatisticsService statisticsService;

    @GetMapping("/heat-map")
    @ResponseStatus(HttpStatus.OK)
    public HeatMap getStatistics(@RequestParam @NotNull final HeatMap.Type type,
            @RequestParam @NotNull final boolean relative) {
        return this.statisticsService.getStatistics(type, relative);
    }
}
