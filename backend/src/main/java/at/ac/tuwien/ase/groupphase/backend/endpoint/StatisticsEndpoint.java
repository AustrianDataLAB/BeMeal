package at.ac.tuwien.ase.groupphase.backend.endpoint;

import at.ac.tuwien.ase.groupphase.backend.dto.HeatMapDto;
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
    public HeatMapDto getStatistics(@RequestParam @NotNull final HeatMapDto.Type type,
            @RequestParam @NotNull final boolean relative, @RequestParam @NotNull final int granularity) {
        return this.statisticsService.getStatistics(type, relative, granularity);
    }
}
