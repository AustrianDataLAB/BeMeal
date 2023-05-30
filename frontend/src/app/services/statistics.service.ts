import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../shared/globals';
import {Observable} from 'rxjs';
import {HeatMap, HeatMapType} from '../dtos/statistics';

@Injectable({
    providedIn: 'root'
})
export class StatisticsService {

    private baseUri: string = this.globals.backendUri + '/statistics';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    public getHeatMap(type: HeatMapType, relative: boolean, granularity: number): Observable<HeatMap> {
        const params = new HttpParams().set('type', type).set('relative', relative).set('granularity', granularity);
        return this.httpClient.get<HeatMap>(`${this.baseUri}/heat-map`, {params: params});
    }
}
