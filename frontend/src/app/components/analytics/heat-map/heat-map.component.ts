import {Component, ElementRef, ViewChild} from '@angular/core';
import {StatisticsService} from '../../../services/statistics.service';
import {HeatMap, HeatMapType} from '../../../dtos/statistics';
import embed, {VisualizationSpec} from 'vega-embed';

@Component({
    selector: 'app-heat-map', templateUrl: './heat-map.component.html', styleUrls: ['./heat-map.component.scss']
})
export class HeatMapComponent {

    @ViewChild('heatMapContainer') heatMapContainer: ElementRef;

    private heatMap: HeatMap;

    constructor(private statisticsService: StatisticsService) {
        statisticsService.getHeatMap(HeatMapType.RANDOM, false).subscribe({
            next: value => {
                this.heatMap = value;
                embed(this.heatMapContainer.nativeElement, this.heatMapSpec(value)).then(r => console.debug(r));
            }, error: err => {
                console.error(err);
            }
        });
    }

    private heatMapSpec(heatMap: HeatMap): VisualizationSpec {
        console.debug("try to plot heatmap", heatMap);
        return {
            $schema: 'https://vega.github.io/schema/vega-lite/v5.json', data: {
                url: '/assets/vega-lite/stat-austria-bez-20230101.topo.json', format: {
                    type: 'topojson', feature: 'STATISTIK_AUSTRIA_POLBEZ_20230101'
                }
            }, mark: {
                type: 'geoshape', stroke: 'white'
            }, width: 1000, height: 1000,
            projection: {
                type: "identity",
                reflectY: true
            },
            transform: [
                {
                    lookup: "properties.g_id",
                    from: {
                        data: {
                            values: heatMap.entries,
                        },
                        key: "id",
                        fields: ["rate"]
                    },
                }
            ],
            encoding: {
                color: {
                    field: "rate",
                    type: "quantitative",
                    title: ""
                }
            }
        };
    }
}

