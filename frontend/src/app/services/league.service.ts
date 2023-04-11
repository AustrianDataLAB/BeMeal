import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Globals} from "../shared/globals";
import {League} from "../dtos/league";
import {map, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LeagueService {

    private authBaseUri: string = this.globals.backendUri + '/league';

    constructor(private httpClient: HttpClient, private globals: Globals) {}

    createLeague(obj: League): Observable<string> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            })
        };
        return this.httpClient.post<any>(this.authBaseUri + '/registration/participant', obj, httpOptions)
            .pipe(map(response => {
                // todo: remove pipe if unnecessary
                //console.log(response);
                return response;
            }));

        return this.httpClient.get<any>(this.authBaseUri + '/test');
    }
}
