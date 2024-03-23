import {Component, ElementRef, Renderer2, ViewChild} from '@angular/core';
import {StatisticsService} from '../../../services/statistics.service';
import {HeatMap, HeatMapType, heatMapTypeToString} from '../../../dtos/statistics';
import embed, {VisualizationSpec} from 'vega-embed';
import {map} from 'rxjs';
import {DarkModeService} from 'angular-dark-mode';

@Component({
    selector: 'app-heat-map', templateUrl: './heat-map.component.html', styleUrls: ['./heat-map.component.scss']
})
export class HeatMapComponent {

    @ViewChild('heatMapContainer') heatMapContainer: ElementRef;
    @ViewChild('topFiveContainer') topFiveContainer: ElementRef;

    private heatMap: HeatMap;
    granularities: Granularity[] = [{
        name: 'District',
        feature: 'STATISTIK_AUSTRIA_POLBEZ_20230101',
        granularity: 3,
        topojson: 'stat-austria-bez-20230101.topo.json'
    }, {
        name: 'Communal',
        feature: 'STATISTIK_AUSTRIA_GEM_20230101',
        granularity: 5,
        topojson: 'stat-austria-gem-20230101.topo.json'
    }];
    showMap = false;
    showList = false;
    darkMode = false;

    listedHeatMapTypes = [HeatMapType.USER_BASE, HeatMapType.SUBMISSIONS, HeatMapType.VOTES, HeatMapType.WINS, HeatMapType.UP_VOTES, HeatMapType.DOWN_VOTES, HeatMapType.USERNAME];

    heatMapType: HeatMapType = HeatMapType.USER_BASE;
    granularity: Granularity = this.granularities[0];
    relative = false;

    constructor(private elementRef: ElementRef, private renderer: Renderer2, private statisticsService: StatisticsService, private darkModeService: DarkModeService) {
        this.darkModeService.darkMode$.subscribe(value => {
            this.darkMode = value;
            this.refreshHeatmap();
        });
        if (localStorage.getItem('bemeal.analytics.showrandom')) {
            const invisibleTypes = [HeatMapType.RANDOM];
            invisibleTypes .push(...this.listedHeatMapTypes);
            this.listedHeatMapTypes = invisibleTypes;
        }
        this.refreshHeatmap();
    }

    protected readonly heatMapTypeToString = heatMapTypeToString;

    private heatMapSpec(heatMap: HeatMap): VisualizationSpec {
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
                    }, key: 'id', fields: ['rate']
                },
            }], encoding: {
                color: {
                    field: 'rate', type: 'quantitative', title: '', legend: {
                        labelColor: this.darkMode ? 'white' : 'black'
                    }
                }
            }, background: 'rgba(255, 255, 255, 0)'
        };
    }

    showMapView() {
        const element = this.elementRef.nativeElement.querySelector('#heatmap-cont');
        const element2 = this.elementRef.nativeElement.querySelector('#topfive-cont');
            //this.renderer.setStyle(document.body, 'filter', 'unset');
        this.renderer.setStyle(element, 'display', 'flex');
        this.renderer.setStyle(element2, 'display', 'none');
    }

    showListView() {
        const element = this.elementRef.nativeElement.querySelector('#heatmap-cont');
        const element2 = this.elementRef.nativeElement.querySelector('#topfive-cont');
        //this.renderer.setStyle(document.body, 'filter', 'unset');
        this.renderer.setStyle(element, 'display', 'none');
        this.renderer.setStyle(element2, 'display', 'flex');
    }

    refreshHeatmap() {
        console.debug('Request statistics with', this.granularity, this.relative, this.heatMapType);
        this.statisticsService.getHeatMap(this.heatMapType, this.relative, this.granularity.granularity).subscribe({
            next: value => {
                this.heatMap = value;
                embed(this.heatMapContainer.nativeElement, this.heatMapSpec(value)).then(r => console.debug(r));
                embed(this.topFiveContainer.nativeElement, this.barChartSpec(value)).then(r => console.debug(r));
            }, error: err => {
                console.error(err);
            }
        });
    }

    private barChartSpec(heatMap: HeatMap): VisualizationSpec {
        const sortedEntries = heatMap.entries.sort((a, b) => a.rate-b.rate);
        let tailedEntries: {id: number, rate: number}[] = [];
        for (let i = 0; i < Math.min(sortedEntries.length, 10); i++) {
            tailedEntries.push(sortedEntries[sortedEntries.length - i - 1]);
        }
        console.debug('tailed entries', tailedEntries);

        if (tailedEntries.length > 0 && tailedEntries[0].rate === 0) {
            tailedEntries = [];
        }

        return {
            $schema: 'https://vega.github.io/schema/vega-lite/v5.json',
            description: 'Bar Chart with a spacing-saving y-axis',
            data: {
                values: tailedEntries
            },
            transform: [{
                lookup: 'id', from: {
                    data: {
                        url: `/assets/vega-lite/${this.granularity.topojson}`, format: {
                            type: 'topojson', feature: this.granularity.feature
                        }
                    }, key: 'properties.g_id', fields: ['properties.g_name']
                },
            },
                ],
            height: {
                step: 50
            },
            mark: {
                type: 'bar', yOffset: 5, cornerRadiusEnd: 2, height: 20, color: this.darkMode ? 'white' : 'black',
            },
            encoding: {
                color: {
                    legend: {
                        labelColor: this.darkMode ? 'white' : 'black',
                        titleColor: this.darkMode ? 'white' : 'black',
                        strokeColor: this.darkMode ? 'white' : 'black',
                    }
                }, y: {
                    field: 'properties.g_name', scale: {padding: 2}, axis: {
                        bandPosition: 0,
                        grid: true,
                        domain: false,
                        ticks: false,
                        labelAlign: 'left',
                        labelBaseline: 'middle',
                        labelPadding: 0,
                        labelOffset: -15,
                        labelColor: this.darkMode ? 'white' : 'black',
                        titleX: 5,
                        titleY: -5,
                        titleAngle: 0,
                        titleAlign: 'left',
                        labelFontSize: 14, // Increase the font size for y-axis labels
                        titleFontSize: 16, // Increase the font size for y-axis title
                        titleColor: this.darkMode ? 'white' : 'black',
                        domainColor: this.darkMode ? 'white' : 'black',
                        tickColor: this.darkMode ? 'white' : 'black',
                    }, sort: '-x', title: this.granularity.name
                }, x: {
                    field: 'rate',
                    aggregate: 'sum',
                    axis: {
                        grid: false,
                        labelColor: this.darkMode ? 'white' : 'black',
                        titleColor: this.darkMode ? 'white' : 'black',
                    },
                    title: 'Amount',
                }
            }, background: 'rgba(255, 255, 255, 0)', width: 1000, title: ''
        };
    }

    protected readonly map = map;
}

interface Granularity {
    name: string,
    feature: string,
    topojson: string,
    granularity: number,
}
