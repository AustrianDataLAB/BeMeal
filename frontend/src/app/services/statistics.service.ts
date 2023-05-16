import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../shared/globals';
import {Observable} from 'rxjs';
import {HeatMap, HeatMapTye} from '../dtos/statistics';

@Injectable({
    providedIn: 'root'
})
export class StatisticsService {

    private baseUri: string = this.globals.backendUri + '/statistics';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    public getHeatMap(type: HeatMapTye, relative: boolean): Observable<HeatMap> {
        const params = new HttpParams().set('type', type).set('relative', relative);
        return this.httpClient.get<HeatMap>(`${this.baseUri}/heat-map`, {params: params});
    }
}
