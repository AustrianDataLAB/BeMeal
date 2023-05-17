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

    granularities: Granularity[] = [{
        name: 'District',
        feature: 'STATISTIK_AUSTRIA_POLBEZ_20230101',
        granularity: 3,
        topojson: 'stat-austria-bez-20230101.topo.json'
    },{
        name: 'Communal',
        feature: 'STATISTIK_AUSTRIA_GEM_20230101',
        granularity: 5,
        topojson: 'stat-austria-gem-20230101.topo.json'
    }]

    granularity: Granularity = this.granularities[0];
    relative = false;

    constructor(private statisticsService: StatisticsService) {
        this.refreshHeatmap();
    }

    refreshHeatmap() {
        this.statisticsService.getHeatMap(HeatMapType.RANDOM, this.relative, this.granularity.granularity).subscribe({
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
                url: `/assets/vega-lite/${this.granularity.topojson}`, format: {
                    type: 'topojson', feature: this.granularity.feature
                }
            }, mark: {
                type: 'geoshape', stroke: 'white'
            }, width: 1000, height: 600, projection: {
                type: 'identity', reflectY: true
            }, transform: [{
                lookup: 'properties.g_id', from: {
                    data: {
                        values: heatMap.entries,
                        },
                        key: "id", fields: ['rate']
                },
            }], encoding: {
                color: {
                    field: 'rate', type: 'quantitative', title: ''
                }
            }, background: 'rgba(255, 255, 255, 0)'
        };
    }
}

interface Granularity {
    name: string,
    feature: string,
    topojson: string,
    granularity: number,
}
