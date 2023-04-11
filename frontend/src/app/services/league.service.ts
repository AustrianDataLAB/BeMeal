import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Globals} from "../shared/globals";
import {League} from "../dtos/league";
import {map, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class LeagueService {

    private baseUri: string = this.globals.backendUri + '/league';

    constructor(private httpClient: HttpClient, private globals: Globals) {}

    createLeague(obj: League): Observable<string> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            })
        };
        console.log(obj);
        return this.httpClient.post<any>(this.baseUri + '/create-league', obj, httpOptions)
            .pipe(map(response => {
                return response;
            }));
    }

    fetchLeagues(): Observable<League[]> {
        return this.httpClient.get<League[]>(this.baseUri + '/leagues');
    }
}
