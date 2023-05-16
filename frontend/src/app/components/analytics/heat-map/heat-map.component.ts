import {Component, ElementRef, ViewChild} from '@angular/core';
import {StatisticsService} from '../../../services/statistics.service';
import {HeatMap, HeatMapTye} from '../../../dtos/statistics';
import embed, {VisualizationSpec} from 'vega-embed';

@Component({
    selector: 'app-heat-map', templateUrl: './heat-map.component.html', styleUrls: ['./heat-map.component.scss']
})
export class HeatMapComponent {

    @ViewChild('heatMapContainer') heatMapContainer: ElementRef;

    private heatMap: HeatMap;

    constructor(private statisticsService: StatisticsService) {
        statisticsService.getHeatMap(HeatMapTye.RANDOM, false).subscribe({
            next: value => {
                this.heatMap = value;
                embed(this.heatMapContainer.nativeElement, this.heatMapSpec()).then(r => console.debug(r));
            }, error: err => {
                console.error(err);
            }
        });
    }

    private heatMapSpec(): VisualizationSpec {
        return {
            $schema: 'https://vega.github.io/schema/vega-lite/v5.json', data: {
                url: '', format: {
                    type: 'topojson', feature: 'gemeinden'
                }
            }, mark: {
                type: 'geoshape', stroke: 'white'
            }, width: 1000, height: 1000
        };
    }
}
